package com.nityapooja.desktop

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.nityapooja.shared.di.desktopPlatformModule
import com.nityapooja.shared.di.sharedModule
import com.nityapooja.shared.ui.NityaPoojaApp
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

fun main() = application {
    // Only start Koin if not already started (prevents crash on window recreate)
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            modules(sharedModule, desktopPlatformModule)
        }
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "NityaPooja - నిత్యపూజ",
        state = rememberWindowState(width = 420.dp, height = 800.dp),
    ) {
        NityaPoojaApp()
    }
}
