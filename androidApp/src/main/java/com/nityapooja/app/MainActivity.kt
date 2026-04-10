package com.nityapooja.app

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.delay
import com.nityapooja.app.data.spotify.SpotifyConnectionStatus
import com.nityapooja.app.data.spotify.SpotifyManager
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.app.ui.components.BannerAd
import com.nityapooja.shared.ui.NityaPoojaApp
import com.nityapooja.app.worker.NotificationWorker
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val preferencesManager: UserPreferencesManager by inject()
    private val spotifyManager: SpotifyManager by inject()

    private var isReady by mutableStateOf(false)

    private val spotifyAuthLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        Log.d("MainActivity", "Spotify auth result: resultCode=${result.resultCode}, data=${result.data}")
        spotifyManager.handleAuthResponse(result.resultCode, result.data)
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* granted or denied — scheduling still works, notifications just won't show if denied */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        splash.setKeepOnScreenCondition { !isReady }
        enableEdgeToEdge()
        requestNotificationPermission()

        maybeRequestReview()

        val deepLinkRoute = intent?.getStringExtra(NotificationWorker.EXTRA_NAV_ROUTE)

        setContent {
            // Hold splash until Compose has rendered its first frame
            LaunchedEffect(Unit) {
                delay(800)
                isReady = true
            }

            val connectionStatus by spotifyManager.connectionStatus.collectAsState()
            val spotifyLinkedPref by preferencesManager.spotifyLinked.collectAsState(initial = false)

            NityaPoojaApp(
                deepLinkRoute = deepLinkRoute,
                onRequestExactAlarmPermission = { requestExactAlarmPermission() },
                onLinkSpotify = {
                    Log.d("MainActivity", "onLinkSpotify tapped, installed=${spotifyManager.isSpotifyInstalled()}")
                    if (spotifyManager.isSpotifyInstalled()) {
                        val intent = spotifyManager.createAuthIntent(this@MainActivity)
                        Log.d("MainActivity", "Launching Spotify auth intent: $intent")
                        spotifyAuthLauncher.launch(intent)
                    } else {
                        spotifyManager.openSpotifyPlayStore()
                    }
                },
                onUnlinkSpotify = {
                    spotifyManager.disconnect()
                },
                spotifyLinked = spotifyLinkedPref || connectionStatus == SpotifyConnectionStatus.CONNECTED,
                spotifyConnecting = connectionStatus == SpotifyConnectionStatus.CONNECTING,
                spotifyInstalled = spotifyManager.isSpotifyInstalled(),
                bannerAd = { BannerAd() },
            )
        }
    }

    /**
     * On Android 12+, SCHEDULE_EXACT_ALARM defaults to denied for new installs targeting API 33+.
     * It cannot be requested via requestPermissions() — must send the user to system settings.
     * AlarmPermissionReceiver will reschedule everything once they grant it.
     */
    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = getSystemService(AlarmManager::class.java)
            if (!am.canScheduleExactAlarms()) {
                startActivity(
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                )
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun maybeRequestReview() {
        val prefs = getSharedPreferences("app_review", Context.MODE_PRIVATE)
        val launchCount = prefs.getInt("launch_count", 0) + 1
        prefs.edit().putInt("launch_count", launchCount).apply()

        // Show review prompt on 5th launch, then every 30 launches
        if (launchCount == 5 || (launchCount > 5 && launchCount % 30 == 0)) {
            val reviewManager = ReviewManagerFactory.create(this)
            reviewManager.requestReviewFlow().addOnSuccessListener { reviewInfo ->
                reviewManager.launchReviewFlow(this, reviewInfo)
            }
        }
    }
}
