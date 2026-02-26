package com.nityapooja.app.di

import com.nityapooja.app.BuildConfig
import com.nityapooja.app.data.spotify.AndroidSpotifyPlaybackBridge
import com.nityapooja.app.data.spotify.SpotifyManager
import com.nityapooja.app.worker.AndroidNotificationScheduler
import com.nityapooja.shared.data.spotify.SpotifyCredentials
import com.nityapooja.shared.platform.NotificationScheduler
import com.nityapooja.shared.platform.SpotifyPlaybackBridge
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidAppModule = module {
    single { SpotifyCredentials(
        clientId = BuildConfig.SPOTIFY_CLIENT_ID,
        clientSecret = BuildConfig.SPOTIFY_CLIENT_SECRET,
    ) }
    single { SpotifyManager(androidContext(), get()) }
    single<SpotifyPlaybackBridge> { AndroidSpotifyPlaybackBridge(get(), get()) }
    single<NotificationScheduler> { AndroidNotificationScheduler(androidContext()) }
}
