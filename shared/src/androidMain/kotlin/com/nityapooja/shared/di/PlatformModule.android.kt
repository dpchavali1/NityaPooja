package com.nityapooja.shared.di

import com.nityapooja.shared.data.local.db.getDatabaseBuilder
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.platform.PlatformAudioPlayer
import com.nityapooja.shared.platform.PlatformHaptics
import com.nityapooja.shared.platform.PlatformSoundEffect
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidPlatformModule = module {
    single { getDatabaseBuilder(androidContext()) }
    single { UserPreferencesManager(androidContext()) }
    single { PlatformAudioPlayer(androidContext()) }
    single { PlatformSoundEffect(androidContext()) }
    single { PlatformHaptics(androidContext()) }
}
