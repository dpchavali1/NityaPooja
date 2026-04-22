package com.nityapooja.shared.platform

import androidx.compose.ui.graphics.ImageBitmap

expect fun shareText(text: String, title: String = "")
expect fun shareImage(bitmap: ImageBitmap, title: String)
