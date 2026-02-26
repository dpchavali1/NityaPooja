package com.nityapooja.shared.platform

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

actual class PlatformHaptics {

    private val lightGenerator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
    private val mediumGenerator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
    private val heavyGenerator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
    private val notificationGenerator = UINotificationFeedbackGenerator()

    actual fun lightTap() {
        lightGenerator.impactOccurred()
    }

    actual fun mediumTap() {
        mediumGenerator.impactOccurred()
    }

    actual fun strongTap() {
        heavyGenerator.impactOccurred()
    }

    actual fun malaComplete() {
        notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
    }

    actual fun uiTap() {
        lightGenerator.impactOccurred()
    }
}
