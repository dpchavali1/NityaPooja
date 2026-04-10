package com.nityapooja.shared.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nityapooja.shared.data.preferences.ThemeMode
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.ui.navigation.NityaPoojaNavHost
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import com.nityapooja.shared.ui.settings.SettingsViewModel
import com.nityapooja.shared.ui.theme.NityaPoojaTheme
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetIn
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Shared app composable providing theme, onboarding, and navigation.
 * Used by all platform entry points (iOS, Desktop, and optionally Android)
 * to get consistent behavior across platforms.
 *
 * @param onLinkSpotify Optional callback for Spotify linking (Android-only feature).
 */
@Composable
fun NityaPoojaApp(
    deepLinkRoute: String? = null,
    onLinkSpotify: (() -> Unit)? = null,
    onUnlinkSpotify: (() -> Unit)? = null,
    onRequestExactAlarmPermission: (() -> Unit)? = null,
    spotifyLinked: Boolean = false,
    spotifyConnecting: Boolean = false,
    spotifyInstalled: Boolean = false,
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val preferencesManager = koinInject<UserPreferencesManager>()
    var onboardingDone by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(Unit) {
        onboardingDone = preferencesManager.onboardingCompleted.first()
    }

    val settingsViewModel: SettingsViewModel = koinViewModel()
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val autoDarkMode by settingsViewModel.autoDarkMode.collectAsState()

    val panchangamViewModel: PanchangamViewModel = koinViewModel()
    val locationInfo by panchangamViewModel.locationInfo.collectAsState()

    val nowInstant by produceState(Clock.System.now()) {
        while (true) {
            delay(60_000L)
            value = Clock.System.now()
        }
    }

    val effectiveThemeMode = if (autoDarkMode && themeMode == ThemeMode.SYSTEM) {
        val now = nowInstant.toLocalDateTime(TimeZone.currentSystemDefault())
        val sunTimes = remember(locationInfo, now.date) {
            val tz = TimeZone.of(locationInfo.timezone)
            val utcOffset = nowInstant.offsetIn(tz).totalSeconds / 3600.0
            panchangamViewModel.calculateSunTimes(
                locationInfo.lat, locationInfo.lng,
                now.year, now.monthNumber, now.dayOfMonth,
                utcOffset,
            )
        }
        val currentDecimal = now.hour + now.minute / 60.0
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
            val onboarding = onboardingDone
            if (onboarding == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                NityaPoojaNavHost(
                    onboardingCompleted = onboarding,
                    deepLinkRoute = deepLinkRoute,
                    onLinkSpotify = onLinkSpotify,
                    onUnlinkSpotify = onUnlinkSpotify,
                    onRequestExactAlarmPermission = onRequestExactAlarmPermission,
                    spotifyLinked = spotifyLinked,
                    spotifyConnecting = spotifyConnecting,
                    spotifyInstalled = spotifyInstalled,
                    bannerAd = bannerAd,
                )
            }
        }
    }
}
