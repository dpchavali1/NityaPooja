package com.nityapooja.shared.di

import org.koin.core.context.startKoin

fun initKoinIos() {
    startKoin {
        modules(sharedModule, iosPlatformModule)
    }
}
