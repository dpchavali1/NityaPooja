package com.nityapooja.shared.di

import com.nityapooja.shared.data.local.db.getDatabaseBuilder
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.data.spotify.SpotifyCredentials
import com.nityapooja.shared.platform.PlatformAudioPlayer
import com.nityapooja.shared.platform.PlatformHaptics
import com.nityapooja.shared.platform.PlatformSoundEffect
import com.nityapooja.shared.platform.NoOpNotificationScheduler
import com.nityapooja.shared.platform.NotificationScheduler
import com.nityapooja.shared.platform.PreviewSpotifyPlaybackBridge
import com.nityapooja.shared.platform.SpotifyPlaybackBridge
import org.koin.dsl.module

val desktopPlatformModule = module {
    single { getDatabaseBuilder() }
    single { UserPreferencesManager() }
    single { PlatformAudioPlayer() }
    single { PlatformSoundEffect() }
    single { PlatformHaptics() }
    single { SpotifyCredentials(
        clientId = System.getenv("SPOTIFY_CLIENT_ID") ?: "",
        clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET") ?: "",
    ) }
    single<SpotifyPlaybackBridge> { PreviewSpotifyPlaybackBridge(get(), get()) }
    single<NotificationScheduler> { NoOpNotificationScheduler() }
}
