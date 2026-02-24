package com.nityapooja.app.ui.japa

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.ui.components.GlassmorphicCard
import com.nityapooja.app.ui.components.SectionHeader
import com.nityapooja.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JapaCounterScreen(
    onBack: () -> Unit = {},
    viewModel: JapaViewModel = hiltViewModel(),
) {
    val count by viewModel.count.collectAsStateWithLifecycle()
    val malas by viewModel.malas.collectAsStateWithLifecycle()
    val totalMalas by viewModel.totalMalas.collectAsStateWithLifecycle()
    val totalCount by viewModel.totalCount.collectAsStateWithLifecycle()
    val todaySessions by viewModel.todaySessions.collectAsStateWithLifecycle()
    val todayMalas by viewModel.todayMalas.collectAsStateWithLifecycle()
    val targetMalas by viewModel.targetMalas.collectAsStateWithLifecycle()
    val mantras by viewModel.mantras.collectAsStateWithLifecycle()
    val selectedMantra by viewModel.selectedMantra.collectAsStateWithLifecycle()
    val currentStreak by viewModel.currentStreak.collectAsStateWithLifecycle()
    val activeDaysCount by viewModel.activeDaysCount.collectAsStateWithLifecycle()

    val view = LocalView.current
    val context = LocalContext.current
    val beadInMala = count % 108
    val progress by animateFloatAsState(
        targetValue = beadInMala / 108f,
        label = "progress",
    )
    val isMalaComplete = count > 0 && beadInMala == 0
    var showResetDialog by remember { mutableStateOf(false) }
    var showMantraSelector by remember { mutableStateOf(false) }
    var showTargetPicker by remember { mutableStateOf(false) }

    // Keep screen on while counting
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    // Reset confirmation dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("రీసెట్ · Reset") },
            text = { Text("Save current session and reset counter?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.saveAndReset()
                    showResetDialog = false
                }) { Text("Save & Reset", color = TempleGold) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            },
        )
    }

    // Mantra selector bottom sheet
    if (showMantraSelector) {
        ModalBottomSheet(
            onDismissRequest = { showMantraSelector = false },
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    "మంత్రం ఎంచుకోండి · Select Mantra",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                mantras.forEach { mantra ->
                    val isSelected = selectedMantra?.id == mantra.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected)
                            TempleGold.copy(alpha = 0.12f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        onClick = {
                            viewModel.selectMantra(mantra)
                            showMantraSelector = false
                        },
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    mantra.titleTelugu,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium,
                                )
                                Text(
                                    mantra.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = TempleGold,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    // Target picker dialog
    if (showTargetPicker) {
        val targets = listOf(1, 3, 5, 7, 11, 21)
        AlertDialog(
            onDismissRequest = { showTargetPicker = false },
            title = { Text("లక్ష్యం · Set Daily Target") },
            text = {
                Column {
                    targets.forEach { target ->
                        val isSelected = targetMalas == target
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected)
                                TempleGold.copy(alpha = 0.12f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            onClick = {
                                viewModel.setTarget(target)
                                showTargetPicker = false
                            },
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    "$target ${if (target == 1) "mala" else "malas"}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) TempleGold else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f),
                                )
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        null,
                                        tint = TempleGold,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("జపం", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Japa Counter", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (count > 0) viewModel.saveAndReset()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (count > 0) {
                        IconButton(onClick = { viewModel.saveAndReset() }) {
                            Icon(Icons.Default.Save, "Save", tint = TempleGold)
                        }
                    }
                    IconButton(onClick = {
                        if (count > 0) showResetDialog = true
                        else {
                            viewModel.saveAndReset() // no-op if 0
                        }
                    }) {
                        Icon(Icons.Default.Refresh, "Reset")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Mantra Selector Chip
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        TempleGold.copy(alpha = 0.3f),
                    ),
                    onClick = { showMantraSelector = true },
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = TempleGold,
                            modifier = Modifier.size(22.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                selectedMantra?.titleTelugu ?: "మంత్రం ఎంచుకోండి",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                selectedMantra?.title ?: "Select a mantra",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Progress Ring + Tap Area
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // Outer progress ring
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(260.dp),
                        color = if (isMalaComplete) AuspiciousGreen else TempleGold,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 12.dp,
                    )

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val tapScale by animateFloatAsState(
                        targetValue = if (isPressed) 0.92f else 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "tapScale",
                    )

                    // Tap circle
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(tapScale)
                            .shadow(
                                12.dp,
                                CircleShape,
                                ambientColor = TempleGold.copy(alpha = 0.3f),
                            )
                            .border(
                                2.dp,
                                if (isMalaComplete) AuspiciousGreen.copy(alpha = 0.6f)
                                else TempleGold.copy(alpha = 0.4f),
                                CircleShape,
                            )
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                            ) {
                                viewModel.increment()
                                val newCount = count + 1
                                performFeedback(view, context, newCount)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "ॐ",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = TempleGold,
                            )
                            Spacer(Modifier.height(2.dp))
                            AnimatedContent(
                                targetState = beadInMala,
                                transitionSpec = {
                                    slideInVertically { -it } + fadeIn() togetherWith
                                        slideOutVertically { it } + fadeOut()
                                },
                                label = "countAnim",
                            ) { displayCount ->
                                Text(
                                    "$displayCount",
                                    style = MaterialTheme.typography.displayLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 52.sp,
                                )
                            }
                            Text(
                                "/ 108",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // Session stats row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    JapaStatCard(
                        label = "మాలలు\nMalas",
                        value = malas.toString(),
                        accentColor = TempleGold,
                        modifier = Modifier.weight(1f),
                    )
                    JapaStatCard(
                        label = "మొత్తం\nTotal",
                        value = count.toString(),
                        accentColor = DeepVermillion,
                        modifier = Modifier.weight(1f),
                    )
                    JapaStatCard(
                        label = "మిగిలింది\nRemaining",
                        value = "${108 - beadInMala}",
                        accentColor = SacredTurmeric,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Daily target progress
            item {
                GlassmorphicCard(
                    cornerRadius = 16.dp,
                    contentPadding = 16.dp,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "నేటి లక్ష్యం · TODAY'S TARGET",
                                style = NityaPoojaTextStyles.GoldLabel,
                                color = TempleGold,
                            )
                            Spacer(Modifier.height(4.dp))
                            val effectiveTodayMalas = todayMalas + malas
                            Text(
                                "$effectiveTodayMalas / $targetMalas malas",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        FilledTonalButton(
                            onClick = { showTargetPicker = true },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = TempleGold.copy(alpha = 0.12f),
                                contentColor = TempleGold,
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Icon(Icons.Default.Flag, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Target", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    val effectiveTodayMalas = todayMalas + malas
                    val targetProgress = if (targetMalas > 0)
                        (effectiveTodayMalas.toFloat() / targetMalas).coerceIn(0f, 1f) else 0f
                    LinearProgressIndicator(
                        progress = { targetProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (targetProgress >= 1f) AuspiciousGreen else TempleGold,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    if (targetProgress >= 1f) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "లక్ష్యం చేరుకున్నారు! Target achieved!",
                            style = MaterialTheme.typography.labelSmall,
                            color = AuspiciousGreen,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            // Streak & Lifetime Stats
            item {
                if (currentStreak > 0) {
                    GlassmorphicCard(
                        cornerRadius = 16.dp,
                        contentPadding = 16.dp,
                        accentColor = AuspiciousGreen,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = DeepVermillion,
                                modifier = Modifier.size(32.dp),
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "$currentStreak ${if (currentStreak == 1) "day" else "days"} streak!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepVermillion,
                                )
                                Text(
                                    "$activeDaysCount total active days",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
                GlassmorphicCard(
                    cornerRadius = 16.dp,
                    contentPadding = 16.dp,
                ) {
                    Text(
                        "జీవితకాల గణాంకాలు · LIFETIME STATS",
                        style = NityaPoojaTextStyles.GoldLabel,
                        color = TempleGold,
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$totalMalas",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = TempleGold,
                            )
                            Text(
                                "మొత్తం మాలలు\nTotal Malas",
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$totalCount",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = TempleGold,
                            )
                            Text(
                                "మొత్తం జపం\nTotal Japa",
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            // Today's Sessions
            if (todaySessions.isNotEmpty()) {
                item {
                    SectionHeader(
                        titleTelugu = "నేటి సెషన్లు",
                        titleEnglish = "Today's Sessions",
                    )
                }
                items(todaySessions) { session ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    session.mantraNameTelugu,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium,
                                )
                                Text(
                                    session.mantraName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "${session.count} counts",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TempleGold,
                                )
                                Text(
                                    "${session.malasCompleted} malas",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(60.dp)) }
        }
    }
}

private fun performFeedback(
    view: android.view.View,
    context: android.content.Context,
    newCount: Int,
) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
    }

    val beadInMala = newCount % 108
    when {
        // Full mala (108) - strongest feedback
        newCount % 108 == 0 -> {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 800)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ toneGen.release() }, 1000)
            } catch (_: Exception) {}
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 150, 80, 250, 80, 400), -1,
                        ),
                    )
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(longArrayOf(0, 150, 80, 250, 80, 400), -1)
                }
            }
        }
        // Half mala (54) - medium-strong feedback
        beadInMala == 54 -> {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 500)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ toneGen.release() }, 600)
            } catch (_: Exception) {}
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 120, 60, 200), -1,
                        ),
                    )
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(longArrayOf(0, 120, 60, 200), -1)
                }
            }
        }
        // Quarter mala (27, 81) - medium feedback
        beadInMala == 27 || beadInMala == 81 -> {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 300)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ toneGen.release() }, 400)
            } catch (_: Exception) {}
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(150)
                }
            }
        }
        // Every 10th bead - light feedback
        newCount % 10 == 0 -> {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(100)
                }
            }
        }
        // Normal bead - subtle tap
        else -> {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(30, 80))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(30)
                }
            }
        }
    }
}

@Composable
private fun JapaStatCard(
    label: String,
    value: String,
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = accentColor,
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
