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
import platform.Foundation.NSBundle
import org.koin.dsl.module

val iosPlatformModule = module {
    single { getDatabaseBuilder() }
    single { UserPreferencesManager() }
    single { PlatformAudioPlayer() }
    single { PlatformSoundEffect() }
    single { PlatformHaptics() }
    single { SpotifyCredentials(
        clientId = NSBundle.mainBundle.objectForInfoDictionaryKey("SPOTIFY_CLIENT_ID") as? String ?: "",
        clientSecret = NSBundle.mainBundle.objectForInfoDictionaryKey("SPOTIFY_CLIENT_SECRET") as? String ?: "",
    ) }
    single<SpotifyPlaybackBridge> { PreviewSpotifyPlaybackBridge(get(), get()) }
    single<NotificationScheduler> { NoOpNotificationScheduler() }
}
