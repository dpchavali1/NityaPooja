package com.nityapooja.app.ui.timer

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.app.ui.components.GlassmorphicCard
import com.nityapooja.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoojaTimerScreen(
    onBack: () -> Unit = {},
) {
    val presetMinutes = listOf(5, 10, 15, 21, 30, 45, 60)
    var selectedMinutes by remember { mutableIntStateOf(15) }
    var totalSeconds by remember { mutableIntStateOf(0) }
    var remainingSeconds by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var bellInterval by remember { mutableIntStateOf(5) } // bell every N minutes
    val bellIntervals = listOf(0, 1, 3, 5, 10) // 0 = off

    val view = LocalView.current

    // Keep screen on
    DisposableEffect(isRunning) {
        if (isRunning) view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    // Timer countdown
    LaunchedEffect(isRunning, isPaused) {
        if (isRunning && !isPaused) {
            while (remainingSeconds > 0) {
                delay(1000)
                if (!isPaused) {
                    remainingSeconds--
                    // Bell at intervals
                    val elapsed = totalSeconds - remainingSeconds
                    if (bellInterval > 0 && elapsed > 0 && elapsed % (bellInterval * 60) == 0) {
                        playBell(volume = 60)
                    }
                }
            }
            if (remainingSeconds == 0 && totalSeconds > 0) {
                // Timer complete - play completion bell
                playBell(volume = 100)
                delay(1500)
                playBell(volume = 100)
                delay(1500)
                playBell(volume = 100)
                isRunning = false
                isPaused = false
            }
        }
    }

    val progress by animateFloatAsState(
        targetValue = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 0f,
        label = "timerProgress",
    )

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("పూజా టైమర్", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Pooja Timer", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Timer Display
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                CircularProgressIndicator(
                    progress = { if (isRunning) progress else 1f },
                    modifier = Modifier.size(260.dp),
                    color = if (remainingSeconds == 0 && totalSeconds > 0) AuspiciousGreen else TempleGold,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 12.dp,
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (!isRunning) {
                        Icon(
                            Icons.Default.Spa,
                            contentDescription = null,
                            tint = TempleGold,
                            modifier = Modifier.size(36.dp),
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    AnimatedContent(
                        targetState = if (isRunning) "$minutes:${seconds.toString().padStart(2, '0')}" else "${selectedMinutes}:00",
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "timer",
                    ) { timeText ->
                        Text(
                            timeText,
                            fontSize = 52.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    if (isRunning && remainingSeconds == 0) {
                        Text(
                            "పూర్తయింది! Complete!",
                            style = MaterialTheme.typography.titleSmall,
                            color = AuspiciousGreen,
                            fontWeight = FontWeight.Bold,
                        )
                    } else if (!isRunning) {
                        Text(
                            "minutes",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Duration Presets (only when not running)
            if (!isRunning) {
                GlassmorphicCard(
                    cornerRadius = 16.dp,
                    contentPadding = 16.dp,
                ) {
                    Text(
                        "సమయం · DURATION",
                        style = NityaPoojaTextStyles.GoldLabel,
                        color = TempleGold,
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        presetMinutes.forEach { min ->
                            val isSelected = selectedMinutes == min
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedMinutes = min },
                                label = { Text("${min}m") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TempleGold.copy(alpha = 0.15f),
                                    selectedLabelColor = TempleGold,
                                ),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                // Bell Interval
                GlassmorphicCard(
                    cornerRadius = 16.dp,
                    contentPadding = 16.dp,
                ) {
                    Text(
                        "గంట · BELL INTERVAL",
                        style = NityaPoojaTextStyles.GoldLabel,
                        color = TempleGold,
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        bellIntervals.forEach { interval ->
                            val isSelected = bellInterval == interval
                            val label = if (interval == 0) "Off" else "${interval}m"
                            FilterChip(
                                selected = isSelected,
                                onClick = { bellInterval = interval },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TempleGold.copy(alpha = 0.15f),
                                    selectedLabelColor = TempleGold,
                                ),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Control Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isRunning) {
                    // Pause/Resume
                    FilledTonalButton(
                        onClick = { isPaused = !isPaused },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                    ) {
                        Icon(
                            if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            null,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(if (isPaused) "Resume" else "Pause")
                    }
                    // Stop
                    Button(
                        onClick = {
                            isRunning = false
                            isPaused = false
                            remainingSeconds = 0
                            totalSeconds = 0
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepVermillion),
                    ) {
                        Icon(Icons.Default.Stop, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Stop")
                    }
                } else {
                    // Start
                    Button(
                        onClick = {
                            totalSeconds = selectedMinutes * 60
                            remainingSeconds = totalSeconds
                            isRunning = true
                            isPaused = false
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TempleGold),
                    ) {
                        Icon(Icons.Default.PlayArrow, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Start Pooja Timer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun playBell(volume: Int = 100) {
    try {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, volume)
        toneGen.startTone(ToneGenerator.TONE_DTMF_D, 1200)
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ toneGen.release() }, 1500)
    } catch (_: Exception) {}
}
