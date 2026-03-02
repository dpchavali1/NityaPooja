package com.nityapooja.shared.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nityapooja.shared.ui.navigation.BottomNavItem
import com.nityapooja.shared.ui.theme.TempleGold

@Composable
fun PremiumBottomBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit,
    onJapaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { clip = false },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 16.dp,
        tonalElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { clip = false }
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Insert FAB in the middle: items[0], items[1], FAB, items[2], items[3], items[4]
            val fabIndex = items.size / 2  // insert FAB at center position

            items.forEachIndexed { index, item ->
                // Insert FAB before the center item
                if (index == fabIndex) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer { clip = false }
                            .offset(y = (-6).dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        FloatingActionButton(
                            onClick = onJapaClick,
                            containerColor = TempleGold,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(56.dp)
                                .shadow(8.dp, CircleShape, ambientColor = TempleGold.copy(alpha = 0.4f)),
                        ) {
                            Icon(
                                Icons.Default.SelfImprovement,
                                contentDescription = "Japa",
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                }

                PremiumNavItem(
                    item = item,
                    isSelected = currentRoute == item.screen.route,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun PremiumNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "navScale",
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) TempleGold else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "navColor",
    )

    Column(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(TempleGold.copy(alpha = 0.15f)),
                )
            }
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = iconColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
        )
    }
}
