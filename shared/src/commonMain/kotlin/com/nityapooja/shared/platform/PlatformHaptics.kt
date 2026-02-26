package com.nityapooja.shared.platform

/**
 * Cross-platform haptic feedback with intensity levels.
 * Mirrors the tiered vibration system from the original Android app.
 */
expect class PlatformHaptics {
    /** Subtle tap — every bead count */
    fun lightTap()

    /** Medium tap — every 10th bead, quarter mala milestones */
    fun mediumTap()

    /** Strong feedback — half mala milestone */
    fun strongTap()

    /** Strongest feedback — full mala (108) completion */
    fun malaComplete()

    /** General UI tap — for offerings, buttons */
    fun uiTap()
}
