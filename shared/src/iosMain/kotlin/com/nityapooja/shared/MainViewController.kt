package com.nityapooja.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController
import com.nityapooja.shared.ui.NityaPoojaApp
import com.nityapooja.shared.ui.components.IosBannerAd
import platform.UIKit.UIView

fun MainViewController(
    bannerAdViewFactory: (() -> UIView)? = null,
) = ComposeUIViewController {
    val bannerAd: (@Composable () -> Unit)? = if (bannerAdViewFactory != null) {
        { IosBannerAd(viewFactory = bannerAdViewFactory) }
    } else {
        null
    }
    NityaPoojaApp(bannerAd = bannerAd)
}
