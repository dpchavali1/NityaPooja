package com.nityapooja.shared.platform

import java.awt.Desktop
import java.net.URI

actual fun openUrl(url: String) {
    try {
        Desktop.getDesktop().browse(URI(url))
    } catch (_: Exception) {
        // Fallback: try open command on macOS
        Runtime.getRuntime().exec(arrayOf("open", url))
    }
}
