package com.nityapooja.shared.platform

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

actual fun shareText(text: String, title: String) {
    // On desktop, copy to clipboard
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(StringSelection(text), null)
}
