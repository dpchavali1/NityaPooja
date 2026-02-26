package com.nityapooja.shared.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual fun openUrl(url: String) {
    println("PlatformUrlOpener: opening URL: $url")
    val nsUrl = NSURL.URLWithString(url) ?: run {
        println("PlatformUrlOpener: invalid URL")
        return
    }
    dispatch_async(dispatch_get_main_queue()) {
        UIApplication.sharedApplication.openURL(
            nsUrl,
            options = emptyMap<Any?, Any>(),
            completionHandler = { success ->
                println("PlatformUrlOpener: openURL result: $success")
            }
        )
    }
}
