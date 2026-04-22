package com.nityapooja.shared.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.ui.theme.TempleGold

/**
 * Wraps [content] inside a MaterialTheme with all body/title/label typography
 * scaled by [fontScale] (= fontSize / 16f). Allows screens to scale ALL text
 * automatically without threading fontScale through every composable.
 */
@Composable
fun ScaledContent(fontScale: Float, content: @Composable () -> Unit) {
    val base = MaterialTheme.typography
    val scaled = base.copy(
        bodyLarge      = base.bodyLarge.copy(fontSize      = (16 * fontScale).sp),
        bodyMedium     = base.bodyMedium.copy(fontSize     = (14 * fontScale).sp),
        bodySmall      = base.bodySmall.copy(fontSize      = (12 * fontScale).sp),
        titleLarge     = base.titleLarge.copy(fontSize     = (22 * fontScale).sp),
        titleMedium    = base.titleMedium.copy(fontSize    = (16 * fontScale).sp),
        titleSmall     = base.titleSmall.copy(fontSize     = (14 * fontScale).sp),
        labelLarge     = base.labelLarge.copy(fontSize     = (14 * fontScale).sp),
        labelMedium    = base.labelMedium.copy(fontSize    = (12 * fontScale).sp),
        labelSmall     = base.labelSmall.copy(fontSize     = (11 * fontScale).sp),
        headlineLarge  = base.headlineLarge.copy(fontSize  = (32 * fontScale).sp),
        headlineMedium = base.headlineMedium.copy(fontSize = (28 * fontScale).sp),
        headlineSmall  = base.headlineSmall.copy(fontSize  = (24 * fontScale).sp),
    )
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        shapes = MaterialTheme.shapes,
        typography = scaled,
    ) {
        content()
    }
}

@Composable
fun FontSizeControls(
    fontSize: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = onDecrease,
            enabled = fontSize > 12,
        ) {
            Text(
                "tt",
                color = if (fontSize > 12) TempleGold else TempleGold.copy(alpha = 0.3f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            "$fontSize",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
        TextButton(
            onClick = onIncrease,
            enabled = fontSize < 28,
        ) {
            Text(
                "TT",
                color = if (fontSize < 28) TempleGold else TempleGold.copy(alpha = 0.3f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
