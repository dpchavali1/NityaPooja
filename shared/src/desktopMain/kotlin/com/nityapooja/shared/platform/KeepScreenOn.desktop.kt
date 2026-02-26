package com.nityapooja.shared.platform

import androidx.compose.runtime.Composable

@Composable
actual fun KeepScreenOn() {
    // No-op on desktop — screen doesn't auto-sleep
}
