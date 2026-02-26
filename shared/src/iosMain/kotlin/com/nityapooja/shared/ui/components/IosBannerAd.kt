package com.nityapooja.shared.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
fun IosBannerAd(viewFactory: () -> UIView) {
    UIKitView(
        factory = { viewFactory() },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
    )
}
