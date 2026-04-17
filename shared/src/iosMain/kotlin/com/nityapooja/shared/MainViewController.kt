package com.nityapooja.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController
import com.nityapooja.shared.ui.NityaPoojaApp
import com.nityapooja.shared.ui.components.IosBannerAd
import platform.StoreKit.SKStoreReviewController
import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIView
import platform.UIKit.UIWindowScene

fun MainViewController(
    bannerAdViewFactory: (() -> UIView)? = null,
    deepLinkRoute: String? = null,
) = ComposeUIViewController {
    val bannerAd: (@Composable () -> Unit)? = if (bannerAdViewFactory != null) {
        { IosBannerAd(viewFactory = bannerAdViewFactory) }
    } else {
        null
    }
    NityaPoojaApp(
        deepLinkRoute = deepLinkRoute,
        bannerAd = bannerAd,
        onRequestReview = ::requestAppReview,
    )
}

private fun requestAppReview() {
    val scene = UIApplication.sharedApplication.connectedScenes
        .mapNotNull { it as? UIWindowScene }
        .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
    scene?.let { SKStoreReviewController.requestReviewInScene(it) }
}
