package com.nityapooja.shared.platform

import androidx.compose.ui.graphics.ImageBitmap
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

actual fun shareText(text: String, title: String) {
    // On desktop, copy to clipboard
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(StringSelection(text), null)
}

actual fun shareImage(bitmap: ImageBitmap, title: String) { /* Desktop not yet implemented */ }
