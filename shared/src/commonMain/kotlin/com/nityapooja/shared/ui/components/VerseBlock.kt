package com.nityapooja.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.ui.theme.NityaPoojaTextStyles
import com.nityapooja.shared.ui.theme.TempleGold

@Composable
fun VerseBlock(
    verseNumber: Int? = null,
    sanskritText: String? = null,
    teluguText: String? = null,
    englishText: String? = null,
    fontScale: Float = 1f,
    modifier: Modifier = Modifier,
) {
    val goldColor = TempleGold.copy(alpha = 0.5f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .drawBehind {
                drawLine(
                    color = goldColor,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 4.dp.toPx(),
                )
            }
            .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 12.dp),
    ) {
        verseNumber?.let {
            Text(
                text = "$it",
                style = NityaPoojaTextStyles.VerseNumber,
                color = TempleGold,
                modifier = Modifier.padding(end = 12.dp, top = 2.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            sanskritText?.let {
                Text(
                    text = it,
                    style = NityaPoojaTextStyles.SanskritVerse.copy(
                        fontSize = (18 * fontScale).sp,
                        lineHeight = (32 * fontScale).sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            teluguText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = (16 * fontScale).sp,
                        lineHeight = (24 * fontScale).sp,
                    ),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            englishText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = (14 * fontScale).sp,
                        lineHeight = (22 * fontScale).sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
