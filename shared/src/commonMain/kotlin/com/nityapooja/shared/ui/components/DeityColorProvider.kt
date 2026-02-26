package com.nityapooja.shared.ui.components

import androidx.compose.ui.graphics.Color
import com.nityapooja.shared.ui.theme.DeityColorMap
import com.nityapooja.shared.ui.theme.TempleGold

/**
 * Safe deity color resolution -- replaces scattered try/catch blocks.
 * KMP-compatible: parses hex color manually instead of using Android's toColorInt().
 */
fun resolveDeityColor(colorTheme: String?): Color {
    if (colorTheme.isNullOrBlank()) return TempleGold
    return DeityColorMap[colorTheme] ?: try {
        Color(colorTheme.removePrefix("#").toLong(16) or 0xFF000000)
    } catch (_: Exception) {
        TempleGold
    }
}
