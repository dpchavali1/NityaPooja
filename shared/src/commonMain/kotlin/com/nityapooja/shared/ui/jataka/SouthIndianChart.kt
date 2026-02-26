package com.nityapooja.shared.ui.jataka

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.ui.theme.TempleGold
import com.nityapooja.shared.ui.theme.TempleGoldLight
import com.nityapooja.shared.utils.JyotishConstants

/**
 * South Indian style birth chart (4x4 grid with merged center).
 * Fixed rashi positions — Mesha always at (0,1).
 * Grahas are displayed as abbreviated Telugu text in their rashi cells.
 * Lagna is marked with "ల" in its rashi cell.
 *
 * Uses Compose Multiplatform text drawing (no Android Canvas/Paint).
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
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val rashiTextSize = with(density) { 9.sp }
    val grahaTextSize = with(density) { 11.sp }
    val lagnaTextSize = with(density) { 10.sp }
    val centerTextSize = with(density) { 14.sp }

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
            drawLine(borderColor, Offset(cellW * i, 0f), Offset(cellW * i, h), strokeWidth = 1.dp.toPx())
            drawLine(borderColor, Offset(0f, cellH * i), Offset(w, cellH * i), strokeWidth = 1.dp.toPx())
        }

        // Clear center 2x2 area
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
        val centerStyle = TextStyle(
            color = borderColor,
            fontSize = centerTextSize,
            fontWeight = FontWeight.Bold,
        )
        val centerLayout = textMeasurer.measure("జాతక చక్రం", centerStyle)
        drawText(
            centerLayout,
            topLeft = Offset(
                w / 2f - centerLayout.size.width / 2f,
                h / 2f - centerLayout.size.height / 2f,
            ),
        )

        // Draw rashi labels and grahas in each cell
        val rashiStyle = TextStyle(color = rashiLabelColor, fontSize = rashiTextSize)
        val grahaStyle = TextStyle(color = textColor, fontSize = grahaTextSize, fontWeight = FontWeight.Bold)
        val lagnaStyle = TextStyle(color = lagnaColor, fontSize = lagnaTextSize, fontWeight = FontWeight.Bold)

        for (rashiIdx in 0..11) {
            val pos = JyotishConstants.SOUTH_INDIAN_POSITIONS[rashiIdx]
            val row = pos[0]
            val col = pos[1]

            // Skip center cells (row 1-2, col 1-2)
            if (row in 1..2 && col in 1..2) continue

            val cellLeft = col * cellW
            val cellTop = row * cellH
            val padding = 4.dp.toPx()

            // Rashi name (top-left of cell)
            val rashiLayout = textMeasurer.measure(JyotishConstants.RASHI_NAMES_TELUGU[rashiIdx], rashiStyle)
            drawText(
                rashiLayout,
                topLeft = Offset(cellLeft + padding, cellTop + padding),
            )

            // Lagna marker
            if (rashiIdx == lagnaRashiIndex) {
                val lagnaLayout = textMeasurer.measure("ల", lagnaStyle)
                drawText(
                    lagnaLayout,
                    topLeft = Offset(
                        cellLeft + cellW - lagnaLayout.size.width - padding,
                        cellTop + padding,
                    ),
                )
            }

            // Graha abbreviations
            val grahas = grahasByRashi[rashiIdx] ?: emptyList()
            val grahaStr = grahas.joinToString(" ") { it.abbreviation }
            if (grahaStr.isNotEmpty()) {
                val grahaLayout = textMeasurer.measure(grahaStr, grahaStyle)
                drawText(
                    grahaLayout,
                    topLeft = Offset(
                        cellLeft + padding,
                        cellTop + cellH / 2f - grahaLayout.size.height / 2f,
                    ),
                )
            }
        }
    }
}
