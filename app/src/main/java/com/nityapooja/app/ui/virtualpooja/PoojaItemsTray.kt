package com.nityapooja.app.ui.virtualpooja

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.app.ui.theme.TempleGold
import com.nityapooja.app.ui.theme.TempleGoldDark

/**
 * Bottom tray with 8 pooja offering buttons in a 4×2 grid,
 * plus a water/milk toggle for abhishekam.
 */
@Composable
fun PoojaItemsTray(
    offerings: Map<PoojaItem, OfferingState>,
    abhishekamType: AbhishekamType,
    showAbhishekamToggle: Boolean,
    onOfferingTap: (PoojaItem) -> Unit,
    onToggleAbhishekamType: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false,
        ) {
            items(PoojaItem.entries) { item ->
                val state = offerings[item]
                PoojaItemButton(
                    item = item,
                    isDone = state?.isDone == true,
                    isAnimating = state?.isAnimating == true,
                    onClick = { onOfferingTap(item) },
                )
            }
        }

        // Water/Milk toggle — briefly shown when abhishekam is tapped, hides after selection
        if (showAbhishekamToggle) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AbhishekamType.entries.forEach { type ->
                    val selected = abhishekamType == type
                    FilterChip(
                        selected = selected,
                        onClick = { if (!selected) onToggleAbhishekamType() },
                        label = {
                            Text(
                                "${type.emoji} ${type.labelTelugu}",
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TempleGold.copy(alpha = 0.2f),
                            selectedLabelColor = TempleGoldDark,
                        ),
                        border = if (selected) BorderStroke(1.dp, TempleGold) else null,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PoojaItemButton(
    item: PoojaItem,
    isDone: Boolean,
    isAnimating: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        label = "pressScale_${item.name}",
    )

    // Bounce animation when offering is made
    val bounceScale by animateFloatAsState(
        targetValue = if (isAnimating) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "bounceScale_${item.name}",
    )

    val borderColor = if (isDone) TempleGold else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (isDone) {
        TempleGold.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier
            .scale(pressScale * bounceScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            width = if (isDone) 1.5.dp else 0.5.dp,
            color = borderColor,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                item.emoji,
                fontSize = 24.sp,
            )
            Text(
                item.labelTelugu,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal,
                color = if (isDone) TempleGold else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Text(
                item.labelEnglish,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}
