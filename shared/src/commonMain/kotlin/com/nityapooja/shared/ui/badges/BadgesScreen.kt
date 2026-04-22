package com.nityapooja.shared.ui.badges

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.data.local.entity.BadgeEntity
import com.nityapooja.shared.data.local.entity.BadgeType
import com.nityapooja.shared.ui.components.FontSizeControls
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.components.ScaledContent
import com.nityapooja.shared.ui.theme.TempleGold
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(
    onBack: () -> Unit,
    viewModel: BadgeViewModel = koinViewModel(),
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val unlockedBadges by viewModel.unlockedBadges.collectAsState()
    val newBadge by viewModel.newBadge.collectAsState()
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f

    val unlockedTypes = unlockedBadges.map { it.badgeType }.toSet()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "పురస్కారాలు",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Spiritual Badges",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    FontSizeControls(
                        fontSize = fontSize,
                        onDecrease = fontSizeViewModel::decrease,
                        onIncrease = fontSizeViewModel::increase,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        ScaledContent(fontScale) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(BadgeType.entries) { badgeType ->
                val isUnlocked = badgeType.name in unlockedTypes
                BadgeCard(
                    badgeType = badgeType,
                    isUnlocked = isUnlocked,
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                bannerAd?.invoke()
            }
        }
        } // ScaledContent
    }

    // Celebration dialog for newly unlocked badge
    if (newBadge != null) {
        BadgeCelebrationDialog(
            badge = newBadge!!,
            onDismiss = { viewModel.dismissNewBadge() },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Badge Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BadgeCard(
    badgeType: BadgeType,
    isUnlocked: Boolean,
) {
    val borderColor = if (isUnlocked) TempleGold else MaterialTheme.colorScheme.outlineVariant
    val borderWidth = if (isUnlocked) 2.dp else 1.dp
    val cardAlpha = if (isUnlocked) 1f else 0.5f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .alpha(cardAlpha),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(borderWidth, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isUnlocked) 4.dp else 0.dp,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Gold shimmer background for unlocked badges
            if (isUnlocked) {
                GoldShimmerBackground()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Emoji or lock
                Box(contentAlignment = Alignment.Center) {
                    if (isUnlocked) {
                        Text(
                            text = badgeType.emoji,
                            fontSize = 44.sp,
                            textAlign = TextAlign.Center,
                        )
                    } else {
                        Text(
                            text = "🔒",
                            fontSize = 44.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Telugu name
                Text(
                    text = badgeType.nameTel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = if (isUnlocked) TempleGold else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                // English name
                Text(
                    text = badgeType.nameEn,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(6.dp))

                // Description
                Text(
                    text = if (isUnlocked) badgeType.descriptionTel else badgeType.descriptionEn,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Gold shimmer background for unlocked cards
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun GoldShimmerBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerOffset",
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0x00FFD700),
            Color(0x18FFD700),
            Color(0x08FFD700),
            Color(0x00FFD700),
        ),
        start = Offset(shimmerOffset * 400f - 200f, 0f),
        end = Offset(shimmerOffset * 400f, 400f),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x12FFD700),
                        Color(0x00FFD700),
                    ),
                    radius = 300f,
                ),
            )
            .background(shimmerBrush),
    )
}

