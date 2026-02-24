package com.nityapooja.app.ui.virtualpooja

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nityapooja.app.data.local.entity.DeityEntity
import com.nityapooja.app.ui.audio.AudioPlayerViewModel
import com.nityapooja.app.ui.components.resolveDeityColor
import com.nityapooja.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualPoojaRoomScreen(
    onBack: () -> Unit,
    audioViewModel: AudioPlayerViewModel,
    viewModel: VirtualPoojaRoomViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current
    val context = LocalContext.current

    // Keep screen on during pooja
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("పూజా గది", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
                // ── Deity selection row ──
                if (uiState.allDeities.isNotEmpty()) {
                    DeitySelectionRow(
                        deities = uiState.allDeities,
                        selectedDeityId = uiState.selectedDeityId,
                        onDeitySelected = { viewModel.selectDeity(it) },
                    )
                }

                // ── Mandir/Altar area (takes remaining space) ──
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

                // ── Pooja items tray (fixed at bottom) ──
                PoojaItemsTray(
                offerings = uiState.offerings,
                abhishekamType = uiState.abhishekamType,
                showAbhishekamToggle = uiState.showAbhishekamToggle,
                onToggleAbhishekamType = { viewModel.toggleAbhishekamType() },
                onOfferingTap = { item ->
                    viewModel.performOffering(item)

                    when (item) {
                        PoojaItem.HARATHI -> {
                            val isNowAnimating = uiState.offerings[PoojaItem.HARATHI]?.isAnimating != true
                            if (isNowAnimating) {
                                // Starting harathi — play aarti audio
                                val url = uiState.harathiAartiUrl
                                if (!url.isNullOrBlank()) {
                                    audioViewModel.playTrack(
                                        url,
                                        uiState.harathiAartiTitle,
                                        uiState.harathiAartiTitleTelugu,
                                    )
                                }
                            } else {
                                // Stopping harathi — stop audio
                                audioViewModel.stop()
                            }
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        }
                        PoojaItem.GHANTA -> {
                            performTempleBellWithReverb(view)
                        }
                        else -> {
                            // Light haptic for other offerings
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        }
                    }
                },
                modifier = Modifier.padding(bottom = 8.dp),
            )
            } // end Column

            // ── Completion banner (overlaid on top, doesn't affect layout) ──
            androidx.compose.animation.AnimatedVisibility(
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

// ═══════════════════════════════════════════════════════════
// Deity selection horizontal row
// ═══════════════════════════════════════════════════════════

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
    val context = LocalContext.current
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

    Column(
        modifier = Modifier
            .width(64.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Deity image circle
        val imageResName = deity.imageResName
        val resId = if (imageResName != null) {
            context.resources.getIdentifier(imageResName, "drawable", context.packageName)
        } else 0

        if (resId != 0) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = deity.nameTelugu,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .border(borderWidth, borderColor, CircleShape),
                contentScale = ContentScale.Crop,
            )
        } else {
            // Fallback: color circle with initial
            Surface(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .border(borderWidth, borderColor, CircleShape),
                color = deityColor.copy(alpha = 0.2f),
            ) {
                Box(contentAlignment = Alignment.Center) {
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

// ═══════════════════════════════════════════════════════════
// Completion banner
// ═══════════════════════════════════════════════════════════

@Composable
private fun CompletionBanner(
    deityName: String,
    onDismiss: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable(onClick = onDismiss),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AuspiciousGreen.copy(alpha = 0.15f),
        ),
        border = BorderStroke(1.dp, AuspiciousGreen),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("🙏", fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "పూజ పూర్తయింది!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AuspiciousGreen,
            )
            Text(
                "$deityName కి పూజ సమర్పించారు",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Pooja Complete · Tap to dismiss",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════
// Temple bell sound (Ghanta) — Synthesized metallic bell
// Uses AudioTrack with multiple harmonics + exponential decay
// for an authentic temple bell resonance
// ═══════════════════════════════════════════════════════════

private fun performTempleBellWithReverb(view: android.view.View) {
    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    Thread {
        try {
            val sampleRate = 44100
            val durationSec = 2.5f
            val numSamples = (sampleRate * durationSec).toInt()
            val samples = ShortArray(numSamples)

            // Temple bell harmonics: fundamental + overtones typical of a brass ghanta
            // Each harmonic: (frequency Hz, relative amplitude, decay rate)
            val harmonics = listOf(
                Triple(520f, 1.0f, 1.8f),     // Fundamental — deep resonant tone
                Triple(1042f, 0.6f, 2.5f),    // 2nd harmonic
                Triple(1570f, 0.35f, 3.2f),   // ~3rd harmonic (slightly inharmonic for metallic sound)
                Triple(2090f, 0.2f, 4.0f),    // 4th harmonic
                Triple(2640f, 0.12f, 5.0f),   // 5th harmonic — shimmer
                Triple(3380f, 0.08f, 6.0f),   // High overtone — metallic ring
                Triple(4200f, 0.04f, 7.5f),   // Very high — bell shimmer
            )

            for (i in 0 until numSamples) {
                val t = i.toFloat() / sampleRate
                var sample = 0.0

                for ((freq, amp, decay) in harmonics) {
                    // Exponential decay for natural bell resonance
                    val envelope = amp * kotlin.math.exp((-decay * t).toDouble())
                    // Add slight beating between close harmonics for realism
                    val beatMod = 1.0 + 0.02 * kotlin.math.sin(2.0 * Math.PI * 1.5 * t)
                    sample += envelope * beatMod * kotlin.math.sin(2.0 * Math.PI * freq * t)
                }

                // Initial strike transient — sharp attack in first 5ms
                val attackEnv = if (t < 0.005f) t / 0.005f else 1.0f
                sample *= attackEnv

                // Clamp and convert to 16-bit
                val clamped = sample.coerceIn(-1.0, 1.0)
                samples[i] = (clamped * Short.MAX_VALUE).toInt().toShort()
            }

            val bufferSize = numSamples * 2 // 16-bit = 2 bytes per sample
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, numSamples)
            audioTrack.play()

            // Release after playback finishes
            Thread.sleep((durationSec * 1000).toLong() + 200)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) { /* ignore on devices without audio support */ }
    }.start()
}

