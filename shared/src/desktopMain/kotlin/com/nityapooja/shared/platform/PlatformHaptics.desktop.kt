package com.nityapooja.shared.platform

actual class PlatformHaptics {
    // Desktop has no haptic feedback hardware
    actual fun lightTap() {}
    actual fun mediumTap() {}
    actual fun strongTap() {}
    actual fun malaComplete() {}
    actual fun uiTap() {}
}
