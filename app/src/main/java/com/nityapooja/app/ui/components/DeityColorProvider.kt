package com.nityapooja.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.nityapooja.app.ui.theme.DeityColorMap
import com.nityapooja.app.ui.theme.TempleGold

/**
 * Safe deity color resolution — replaces scattered try/catch blocks.
 */
fun resolveDeityColor(colorTheme: String?): Color {
    if (colorTheme.isNullOrBlank()) return TempleGold
    return DeityColorMap[colorTheme] ?: try {
        Color(colorTheme.toColorInt())
    } catch (_: Exception) {
        TempleGold
    }
}
