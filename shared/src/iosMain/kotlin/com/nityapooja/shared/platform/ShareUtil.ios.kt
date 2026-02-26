package com.nityapooja.shared.platform

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

actual fun shareText(text: String, title: String) {
    val items = listOf(text)
    val activityVC = UIActivityViewController(activityItems = items, applicationActivities = null)

    val scene = UIApplication.sharedApplication.connectedScenes.firstOrNull() as? UIWindowScene
    val keyWindow = scene?.windows?.firstOrNull { (it as? UIWindow)?.isKeyWindow() == true } as? UIWindow
    var topVC = keyWindow?.rootViewController
    while (topVC?.presentedViewController != null) {
        topVC = topVC.presentedViewController
    }
    topVC?.presentViewController(activityVC, animated = true, completion = null)
}
