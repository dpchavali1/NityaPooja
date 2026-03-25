package com.nityapooja.shared.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.ui.panchangam.PanchangamData
import com.nityapooja.shared.ui.theme.TempleGold

@Composable
fun TeluguCalendarCard(
    panchangamData: PanchangamData,
    fontScale: Float = 1f,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassmorphicCard(
        modifier = modifier,
        accentColor = TempleGold,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Samvatsaram
                Text(
                    "శ్రీ ${panchangamData.samvatsara.nameTelugu} నామ సంవత్సరం",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = (16 * fontScale).sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = TempleGold,
                )

                Spacer(Modifier.height(8.dp))

                // Masam + Paksham + Tithi
                Text(
                    "${panchangamData.masa.nameTelugu} · ${panchangamData.tithi.pakshaTelugu} · ${panchangamData.tithi.nameTelugu}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = (15 * fontScale).sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(Modifier.height(4.dp))

                // Vaaram + Nakshatra
                Text(
                    "${panchangamData.teluguDay} · ${panchangamData.nakshatra.nameTelugu}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = (14 * fontScale).sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(Modifier.height(4.dp))

                // Ayana + Rutu
                Text(
                    "${panchangamData.ayana.nameTelugu} · ${panchangamData.rutu.nameTelugu}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = (12 * fontScale).sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Calendar icon + Gregorian date
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 12.dp),
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = "Panchangam",
                    tint = TempleGold,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    panchangamData.dateDisplay,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = (11 * fontScale).sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
