package com.nityapooja.shared.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.ui.theme.TempleGold

/**
 * Reusable bottom sheet for explaining a Hindu concept to first-time / young users.
 *
 * @param titleTelugu   Telugu title of the concept
 * @param titleEnglish  English title
 * @param bodyTelugu    2–3 sentence Telugu explanation
 * @param bodyEnglish   2–3 sentence English explanation
 * @param whyItMatters  Optional: "Why it matters today / in general" — shown in a tinted box
 * @param tips          Optional list of practical tip strings (shown as bullet points)
 * @param onDismiss     Called when the sheet is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(
    titleTelugu: String,
    titleEnglish: String,
    bodyTelugu: String,
    bodyEnglish: String,
    whyItMatters: String? = null,
    tips: List<String> = emptyList(),
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = TempleGold, modifier = Modifier.size(22.dp))
                    Column {
                        Text(titleTelugu, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TempleGold)
                        Text(titleEnglish, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            HorizontalDivider(color = TempleGold.copy(alpha = 0.2f))

            // Telugu body
            Text(bodyTelugu, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp), lineHeight = 24.sp)

            // English body
            Text(
                bodyEnglish,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp,
            )

            // Why it matters box
            if (whyItMatters != null) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = TempleGold.copy(alpha = 0.08f),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("✦", color = TempleGold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            whyItMatters,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp,
                        )
                    }
                }
            }

            // Tips
            if (tips.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text("చిట్కాలు · Tips", style = MaterialTheme.typography.labelLarge, color = TempleGold, fontWeight = FontWeight.Bold)
                tips.forEach { tip ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("•", color = TempleGold, fontWeight = FontWeight.Bold)
                        Text(tip, style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp), lineHeight = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
