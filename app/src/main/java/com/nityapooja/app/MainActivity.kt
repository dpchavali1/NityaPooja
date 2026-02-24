package com.nityapooja.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.data.preferences.ThemeMode
import com.nityapooja.app.data.preferences.UserPreferencesManager
import com.nityapooja.app.data.spotify.SpotifyManager
import com.nityapooja.app.ui.navigation.NityaPoojaNavHost
import com.nityapooja.app.ui.panchangam.PanchangamViewModel
import com.nityapooja.app.ui.settings.SettingsViewModel
import com.nityapooja.app.ui.theme.NityaPoojaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var preferencesManager: UserPreferencesManager
    @Inject lateinit var spotifyManager: SpotifyManager

    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Read onboarding state synchronously (DataStore is fast for local reads)
        val onboardingDone = runBlocking { preferencesManager.onboardingCompleted.first() }
        isReady = true

        splash.setKeepOnScreenCondition { !isReady }
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle()
            val autoDarkMode by settingsViewModel.autoDarkMode.collectAsStateWithLifecycle()

            val panchangamViewModel: PanchangamViewModel = hiltViewModel()
            val locationInfo by panchangamViewModel.locationInfo.collectAsStateWithLifecycle()

            val effectiveThemeMode = if (autoDarkMode && themeMode == ThemeMode.SYSTEM) {
                val now = remember { Calendar.getInstance() }
                val sunTimes = remember(locationInfo) {
                    val year = now.get(Calendar.YEAR)
                    val month = now.get(Calendar.MONTH) + 1
                    val day = now.get(Calendar.DAY_OF_MONTH)
                    val tz = java.util.TimeZone.getTimeZone(locationInfo.timezone)
                    val utcOffset = tz.getOffset(now.timeInMillis) / 3600000.0
                    panchangamViewModel.calculateSunTimes(locationInfo.lat, locationInfo.lng, year, month, day, utcOffset)
                }
                val currentDecimal = now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0
                if (currentDecimal < sunTimes.sunriseDecimal || currentDecimal > sunTimes.sunsetDecimal) {
                    ThemeMode.DARK
                } else {
                    ThemeMode.LIGHT
                }
            } else {
                themeMode
            }

            val forceDark: Boolean? = when (effectiveThemeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SAFFRON -> false
                ThemeMode.SYSTEM -> null
            }
            val isSaffron = effectiveThemeMode == ThemeMode.SAFFRON

            NityaPoojaTheme(forceDark = forceDark, saffronTheme = isSaffron) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NityaPoojaNavHost(
                        onboardingCompleted = onboardingDone,
                        onLinkSpotify = { spotifyManager.startAuth(this@MainActivity) },
                    )
                }
            }
        }
    }

    @Deprecated("Use Activity Result API", replaceWith = ReplaceWith("registerForActivityResult"))
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        android.util.Log.d("SpotifyAuth", "onActivityResult: requestCode=$requestCode resultCode=$resultCode data=$data")
        if (requestCode == SpotifyManager.AUTH_REQUEST_CODE) {
            spotifyManager.handleAuthResponse(resultCode, data)
        }
    }
}
