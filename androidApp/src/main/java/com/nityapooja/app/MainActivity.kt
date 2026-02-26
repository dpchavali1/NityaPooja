package com.nityapooja.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nityapooja.app.data.spotify.SpotifyConnectionStatus
import com.nityapooja.app.data.spotify.SpotifyManager
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.ui.NityaPoojaApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val preferencesManager: UserPreferencesManager by inject()
    private val spotifyManager: SpotifyManager by inject()

    private var isReady = false

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

        runBlocking { preferencesManager.onboardingCompleted.first() }
        isReady = true

        splash.setKeepOnScreenCondition { !isReady }
        enableEdgeToEdge()
        requestNotificationPermission()

        setContent {
            val connectionStatus by spotifyManager.connectionStatus.collectAsState()
            val spotifyLinkedPref by preferencesManager.spotifyLinked.collectAsState(initial = false)

            NityaPoojaApp(
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
            )
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
}
