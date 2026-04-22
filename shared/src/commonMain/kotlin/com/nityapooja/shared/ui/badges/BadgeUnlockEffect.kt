package com.nityapooja.shared.ui.badges

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.data.local.entity.BadgeType
import com.nityapooja.shared.ui.theme.TempleGold

/**
 * Drop-in composable that watches [BadgeViewModel.newBadge] and shows a
 * celebration dialog whenever a badge is newly unlocked. Add this once
 * to any screen that triggers badge unlocks.
 */
@Composable
fun BadgeUnlockEffect(viewModel: BadgeViewModel) {
    val newBadge by viewModel.newBadge.collectAsState()
    newBadge?.let { badge ->
        BadgeCelebrationDialog(badge = badge, onDismiss = { viewModel.dismissNewBadge() })
    }
}

@Composable
fun BadgeCelebrationDialog(badge: BadgeType, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(badge.emoji, fontSize = 64.sp, textAlign = TextAlign.Center)
                Text(
                    badge.nameTel,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TempleGold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    badge.nameEn,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    "అభినందనలు! మీరు ${badge.nameTel} పురస్కారం పొందారు!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    badge.descriptionTel,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = TempleGold),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("సరే! / Great!", fontWeight = FontWeight.SemiBold)
            }
        },
    )
}
