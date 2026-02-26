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
