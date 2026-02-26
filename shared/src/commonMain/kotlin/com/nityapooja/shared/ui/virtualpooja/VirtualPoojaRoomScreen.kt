package com.nityapooja.shared.ui.virtualpooja

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.data.local.entity.DeityEntity
import com.nityapooja.shared.platform.PlatformHaptics
import com.nityapooja.shared.platform.PlatformSoundEffect
import com.nityapooja.shared.ui.audio.AudioPlayerViewModel
import com.nityapooja.shared.ui.components.getDeityDrawable
import com.nityapooja.shared.ui.components.resolveDeityColor
import com.nityapooja.shared.ui.theme.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualPoojaRoomScreen(
    onBack: () -> Unit,
    audioViewModel: AudioPlayerViewModel,
    viewModel: VirtualPoojaRoomViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val soundEffect = koinInject<PlatformSoundEffect>()
    val haptics = koinInject<PlatformHaptics>()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("\u0C2A\u0C42\u0C1C\u0C3E \u0C17\u0C26\u0C3F", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Virtual Pooja Room", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.resetPooja()
                        audioViewModel.stop()
                    }) {
                        Icon(Icons.Default.Refresh, "Reset", tint = TempleGold)
                    }
                },
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Main content column
            Column(modifier = Modifier.fillMaxSize()) {
                // Deity selection row
                if (uiState.allDeities.isNotEmpty()) {
                    DeitySelectionRow(
                        deities = uiState.allDeities,
                        selectedDeityId = uiState.selectedDeityId,
                        onDeitySelected = { viewModel.selectDeity(it) },
                    )
                }

                // Mandir/Altar area (takes remaining space)
                MandirAltarArea(
                    deity = uiState.selectedDeity,
                    offerings = uiState.offerings,
                    abhishekamType = uiState.abhishekamType,
                    floatingPetals = uiState.floatingPetals,
                    smokeParticles = uiState.smokeParticles,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )

                // Pooja items tray (fixed at bottom)
                PoojaItemsTray(
                    offerings = uiState.offerings,
                    abhishekamType = uiState.abhishekamType,
                    showAbhishekamToggle = uiState.showAbhishekamToggle,
                    onToggleAbhishekamType = { viewModel.toggleAbhishekamType() },
                    onOfferingTap = { item ->
                        viewModel.performOffering(item)
                        haptics.uiTap() // Haptic feedback on every offering

                        when (item) {
                            PoojaItem.GHANTA -> {
                                haptics.uiTap() // Mild haptic for bell
                                soundEffect.playBellSound()
                            }
                            PoojaItem.HARATHI -> {
                                val isNowAnimating = uiState.offerings[PoojaItem.HARATHI]?.isAnimating != true
                                if (isNowAnimating) {
                                    audioViewModel.playViaSpotify(
                                        "om jai jagdish hare anuradha paudwal",
                                        "Om Jai Jagdish Hare",
                                        "\u0C13\u0C02 \u0C1C\u0C48 \u0C1C\u0C17\u0C26\u0C40\u0C36\u0C4D \u0C39\u0C30\u0C47",
                                    )
                                } else {
                                    audioViewModel.stop()
                                }
                            }
                            else -> {
                                // No special sound for other offerings
                            }
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            } // end Column

            // Completion banner (overlaid on top, doesn't affect layout)
            AnimatedVisibility(
                visible = uiState.showCompletionBanner,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter),
            ) {
                CompletionBanner(
                    deityName = uiState.selectedDeity?.nameTelugu ?: "",
                    onDismiss = { viewModel.dismissCompletionBanner() },
                )
            }
        } // end Box
    }
}

// Deity selection horizontal row

@Composable
private fun DeitySelectionRow(
    deities: List<DeityEntity>,
    selectedDeityId: Int?,
    onDeitySelected: (Int) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(deities) { deity ->
            DeityChip(
                deity = deity,
                isSelected = deity.id == selectedDeityId,
                onClick = { onDeitySelected(deity.id) },
            )
        }
    }
}

@Composable
private fun DeityChip(
    deity: DeityEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val deityColor = resolveDeityColor(deity.colorTheme)
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) TempleGold else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(300),
        label = "chipBorder_${deity.id}",
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 1.dp,
        animationSpec = tween(300),
        label = "chipBorderWidth_${deity.id}",
    )

    val drawableRes = deity.imageResName?.let { getDeityDrawable(it) }

    Column(
        modifier = Modifier
            .width(64.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .border(borderWidth, borderColor, CircleShape),
            color = deityColor.copy(alpha = 0.2f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (drawableRes != null) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(drawableRes),
                        contentDescription = deity.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Text(
                        deity.nameTelugu.take(1),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = deityColor,
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            deity.nameTelugu,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) TempleGold else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

// Completion banner

@Composable
private fun CompletionBanner(
    deityName: String,
    onDismiss: () -> Unit,
) {
    val titleColor = androidx.compose.ui.graphics.Color.White
    val bodyColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f)
    val subtitleColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.75f)

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable(onClick = onDismiss),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AuspiciousGreen,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("\uD83D\uDE4F", fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "\u0C2A\u0C42\u0C1C \u0C2A\u0C42\u0C30\u0C4D\u0C24\u0C2F\u0C3F\u0C02\u0C26\u0C3F!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor,
            )
            Text(
                "$deityName \u0C15\u0C3F \u0C2A\u0C42\u0C1C \u0C38\u0C2E\u0C30\u0C4D\u0C2A\u0C3F\u0C02\u0C1A\u0C3E\u0C30\u0C41",
                style = MaterialTheme.typography.bodyMedium,
                color = bodyColor,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Pooja Complete \u00B7 Tap to dismiss",
                style = MaterialTheme.typography.bodySmall,
                color = subtitleColor,
            )
        }
    }
}
