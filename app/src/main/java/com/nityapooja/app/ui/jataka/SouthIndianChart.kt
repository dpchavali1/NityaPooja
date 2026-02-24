package com.nityapooja.app.ui.jataka

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import android.graphics.Paint
import android.graphics.Typeface
import com.nityapooja.app.ui.theme.TempleGold
import com.nityapooja.app.ui.theme.TempleGoldLight
import com.nityapooja.app.utils.JyotishConstants

/**
 * South Indian style birth chart (4×4 grid with merged center).
 * Fixed rashi positions — Mesha always at (0,1).
 * Grahas are displayed as abbreviated Telugu text in their rashi cells.
 * Lagna is marked with "ల" in its rashi cell.
 */
@Composable
fun SouthIndianChart(
    positions: List<GrahaPosition>,
    lagnaRashiIndex: Int,
    modifier: Modifier = Modifier,
    borderColor: Color = TempleGold,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    rashiLabelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    lagnaColor: Color = TempleGoldLight,
) {
    val density = LocalDensity.current
    val textSizePx = with(density) { 11.dp.toPx() }
    val rashiTextSizePx = with(density) { 9.dp.toPx() }
    val lagnaTextSizePx = with(density) { 10.dp.toPx() }
    val centerTextSizePx = with(density) { 14.dp.toPx() }

    // Group grahas by rashi
    val grahasByRashi = positions.groupBy { it.rashiIndex }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp)
    ) {
        val w = size.width
        val h = size.height
        val cellW = w / 4f
        val cellH = h / 4f

        // Draw outer border
        drawRect(
            color = borderColor,
            topLeft = Offset.Zero,
            size = Size(w, h),
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw grid lines
        for (i in 1..3) {
            // Vertical
            drawLine(borderColor, Offset(cellW * i, 0f), Offset(cellW * i, h), strokeWidth = 1.dp.toPx())
            // Horizontal
            drawLine(borderColor, Offset(0f, cellH * i), Offset(w, cellH * i), strokeWidth = 1.dp.toPx())
        }

        // Clear center 2×2 area by drawing a filled rect
        drawRect(
            color = Color.Transparent,
            topLeft = Offset(cellW, cellH),
            size = Size(cellW * 2, cellH * 2),
        )

        // Draw center box border
        drawRect(
            color = borderColor,
            topLeft = Offset(cellW, cellH),
            size = Size(cellW * 2, cellH * 2),
            style = Stroke(width = 1.5f.dp.toPx())
        )

        // Center text: "జాతక చక్రం"
        val centerPaint = Paint().apply {
            color = borderColor.toArgbCompat()
            textSize = centerTextSizePx
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        drawContext.canvas.nativeCanvas.drawText(
            "జాతక చక్రం",
            w / 2f,
            h / 2f + centerTextSizePx / 3f,
            centerPaint
        )

        // Draw rashi labels and grahas in each cell
        val rashiLabelPaint = Paint().apply {
            color = rashiLabelColor.toArgbCompat()
            textSize = rashiTextSizePx
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }

        val grahaPaint = Paint().apply {
            color = textColor.toArgbCompat()
            textSize = textSizePx
            textAlign = Paint.Align.LEFT
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val lagnaPaint = Paint().apply {
            color = lagnaColor.toArgbCompat()
            textSize = lagnaTextSizePx
            textAlign = Paint.Align.LEFT
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        for (rashiIdx in 0..11) {
            val pos = JyotishConstants.SOUTH_INDIAN_POSITIONS[rashiIdx]
            val row = pos[0]
            val col = pos[1]

            // Skip center cells (row 1-2, col 1-2) — they're the merged center
            if (row in 1..2 && col in 1..2) continue

            val cellLeft = col * cellW
            val cellTop = row * cellH
            val padding = 4.dp.toPx()

            // Rashi name (top-left of cell)
            drawContext.canvas.nativeCanvas.drawText(
                JyotishConstants.RASHI_NAMES_TELUGU[rashiIdx],
                cellLeft + padding,
                cellTop + rashiTextSizePx + padding,
                rashiLabelPaint
            )

            // Lagna marker
            if (rashiIdx == lagnaRashiIndex) {
                drawContext.canvas.nativeCanvas.drawText(
                    "ల",
                    cellLeft + cellW - lagnaTextSizePx - padding,
                    cellTop + lagnaTextSizePx + padding,
                    lagnaPaint
                )
            }

            // Graha abbreviations
            val grahas = grahasByRashi[rashiIdx] ?: emptyList()
            val grahaStr = grahas.joinToString(" ") { it.abbreviation }
            if (grahaStr.isNotEmpty()) {
                drawContext.canvas.nativeCanvas.drawText(
                    grahaStr,
                    cellLeft + padding,
                    cellTop + cellH / 2f + textSizePx / 2f,
                    grahaPaint
                )
            }
        }
    }
}

private fun Color.toArgbCompat(): Int {
    return android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt(),
    )
}
