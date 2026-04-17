package com.nityapooja.shared.ui.japa

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.platform.KeepScreenOn
import com.nityapooja.shared.platform.PlatformHaptics
import com.nityapooja.shared.platform.shareText
import com.nityapooja.shared.ui.components.GlassmorphicCard
import com.nityapooja.shared.ui.components.SectionHeader
import com.nityapooja.shared.ui.theme.AuspiciousGreen
import com.nityapooja.shared.ui.theme.DeepVermillion
import com.nityapooja.shared.ui.theme.NityaPoojaTextStyles
import com.nityapooja.shared.ui.theme.SacredTurmeric
import com.nityapooja.shared.ui.theme.TempleGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JapaCounterScreen(
    onBack: () -> Unit = {},
    onRequestReview: () -> Unit = {},
    viewModel: JapaViewModel = koinViewModel(),
) {
    val count by viewModel.count.collectAsState()
    val malas by viewModel.malas.collectAsState()
    val todaySessions by viewModel.todaySessions.collectAsState()
    val todayMalas by viewModel.todayMalas.collectAsState()
    val targetMalas by viewModel.targetMalas.collectAsState()
    val mantras by viewModel.mantras.collectAsState()
    val selectedMantra by viewModel.selectedMantra.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val longestStreak by viewModel.longestStreak.collectAsState()
    val last7DaysActivity by viewModel.last7DaysActivity.collectAsState()
    val activeDaysCount by viewModel.activeDaysCount.collectAsState()
    val elapsedSeconds by viewModel.sessionElapsedSeconds.collectAsState()
    val lastTapMs by viewModel.lastTapMs.collectAsState()
    val malaCompleteEvent by viewModel.malaCompleteEvent.collectAsState()
    val selectedMantraLifetimeMalas by viewModel.selectedMantraLifetimeMalas.collectAsState()
    val shareAchievement by viewModel.shareAchievement.collectAsState()
    val nudgeReview by viewModel.nudgeReview.collectAsState()

    val haptics = koinInject<PlatformHaptics>()
    KeepScreenOn()

    // UI state
    var showResetDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }
    var showMantraSelector by remember { mutableStateOf(false) }
    var showTargetPicker by remember { mutableStateOf(false) }
    var focusMode by rememberSaveable { mutableStateOf(true) }
    var malaJustCompleted by remember { mutableStateOf(false) }
    var tapPaused by remember { mutableStateOf(false) }
    var showUndo by remember { mutableStateOf(false) }
    var glowActive by remember { mutableStateOf(false) }

    // Mala complete: pause tap + banner, separate glow
    LaunchedEffect(malaCompleteEvent) {
        if (malaCompleteEvent > 0) {
            haptics.malaComplete()
            tapPaused = true
            malaJustCompleted = true
            delay(1500L)
            tapPaused = false
            delay(800L)
            malaJustCompleted = false
        }
    }

    LaunchedEffect(malaCompleteEvent) {
        if (malaCompleteEvent > 0) {
            glowActive = true
            delay(1200L)
            glowActive = false
        }
    }

    // Review nudge: fire Play/App Store review at milestone mala counts
    LaunchedEffect(nudgeReview) {
        if (nudgeReview) {
            delay(2000L) // let mala celebration finish first
            onRequestReview()
            viewModel.dismissReviewNudge()
        }
    }

    // Undo window: show undo button for 2 seconds after a tap
    LaunchedEffect(lastTapMs) {
        if (lastTapMs > 0L) {
            showUndo = true
            delay(2000L)
            showUndo = false
        }
    }

    val glowAlpha by animateFloatAsState(
        targetValue = if (glowActive) 0.7f else 0f,
        animationSpec = tween(if (glowActive) 150 else 900),
        label = "glowAlpha",
    )
    val glowScale by animateFloatAsState(
        targetValue = if (glowActive) 1.06f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "glowScale",
    )
    val progressColor by animateColorAsState(
        targetValue = if (glowActive) AuspiciousGreen else TempleGold,
        animationSpec = tween(300),
        label = "progressColor",
    )

    // Idle breathing — only when count == 0
    val breathTransition = rememberInfiniteTransition("breath")
    val breathScale by breathTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathScale",
    )

    val beadInMala = count % 108
    val progress by animateFloatAsState(
        targetValue = beadInMala / 108f,
        label = "progress",
    )

    // ── Dialogs ──────────────────────────────────────────────────────────────

    if (showBackDialog) {
        val contextText = when {
            malas > 0 -> "$count counts, $malas mala${if (malas > 1) "s" else ""} in this session."
            count > 0 -> "$count counts in this session. Save before leaving?"
            else -> "Leave without saving?"
        }
        AlertDialog(
            onDismissRequest = { showBackDialog = false },
            title = { Text("జపం నిలిపేయాలా?") },
            text = { Text(contextText) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.saveAndReset()
                    showBackDialog = false
                    onBack()
                }) { Text("Save & Exit", color = TempleGold) }
            },
            dismissButton = {
                TextButton(onClick = { showBackDialog = false }) { Text("Continue") }
            },
        )
    }

    if (showResetDialog) {
        val contextText = when {
            malas > 0 -> "Save $malas mala${if (malas > 1) "s" else ""} ($count counts) and reset?"
            count > 0 -> "Save $count counts and reset the counter?"
            else -> "Reset the counter?"
        }
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("రీసెట్ · Reset") },
            text = { Text(contextText) },
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

    if (showSaveDialog) {
        val contextText = when {
            malas > 0 -> "Save $malas mala${if (malas > 1) "s" else ""} ($count counts) to today's sessions?"
            count > 0 -> "Save $count counts to today's sessions?"
            else -> "Nothing to save yet."
        }
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("జపం సేవ్ · Save Session") },
            text = { Text(contextText) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.saveAndReset()
                        showSaveDialog = false
                    },
                    enabled = count > 0,
                ) { Text("Save", color = TempleGold) }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) { Text("Cancel") }
            },
        )
    }

    // ── Mala achievement share sheet ─────────────────────────────────────────

    shareAchievement?.let { data ->
        ModalBottomSheet(
            onDismissRequest = viewModel::dismissShareAchievement,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("🎉", fontSize = 48.sp, modifier = Modifier.padding(bottom = 8.dp))
                Text(
                    "మాల పూర్తయింది!",
                    style = MaterialTheme.typography.titleLarge,
                    color = TempleGold,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${data.malas} మాల · ${data.mantraNameTelugu}",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                )
                if (data.streak > 1) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "🔥 ${data.streak} రోజుల స్ట్రీక్!",
                        fontSize = 14.sp,
                        color = DeepVermillion,
                    )
                }
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TextButton(
                        onClick = viewModel::dismissShareAchievement,
                        modifier = Modifier.weight(1f),
                    ) { Text("Skip") }
                    FilledTonalButton(
                        onClick = {
                            val malasText = if (data.malas == 1) "1 mala" else "${data.malas} malas"
                            val streakLine = if (data.streak > 1) "\n🔥 ${data.streak}-day streak!" else ""
                            shareText(
                                "🙏 Completed $malasText of ${data.mantraNameTelugu} (${data.mantraName}) today!$streakLine\n\nJoin me on NityaPooja 📿\nhttps://play.google.com/store/apps/details?id=com.nityapooja.app",
                                "My Japa Achievement",
                            )
                            viewModel.dismissShareAchievement()
                        },
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                            containerColor = TempleGold.copy(alpha = 0.15f),
                            contentColor = TempleGold,
                        ),
                    ) {
                        Text("Share Achievement")
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // ── Mantra selector bottom sheet ─────────────────────────────────────────

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

    // ── Target picker dialog ─────────────────────────────────────────────────

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

    // ── Main scaffold ────────────────────────────────────────────────────────

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "జపం",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Japa Counter",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (count > 0) showBackDialog = true else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (count > 0) {
                        IconButton(onClick = { showSaveDialog = true }) {
                            Icon(Icons.Default.Save, "Save", tint = TempleGold)
                        }
                    }
                    IconButton(onClick = {
                        if (count > 0) showResetDialog = true
                    }) {
                        Icon(Icons.Default.Refresh, "Reset")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // ── Mantra Chip ──────────────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.3f)),
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
                            selectedMantra?.title ?: "Tap to select a mantra",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        selectedMantra?.sanskrit?.let { sanskrit ->
                            Spacer(Modifier.height(4.dp))
                            Text(
                                sanskrit,
                                fontSize = 13.sp,
                                lineHeight = 21.sp,
                                color = TempleGold.copy(alpha = 0.9f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    // Lifetime malas chip for selected mantra
                    if (selectedMantraLifetimeMalas > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TempleGold.copy(alpha = 0.12f),
                        ) {
                            Text(
                                "$selectedMantraLifetimeMalas మాలలు",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = TempleGold,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // ── Mala complete banner (sits above tap zone) ───────────────────
            AnimatedVisibility(
                visible = malaJustCompleted,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
            ) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = AuspiciousGreen,
                ) {
                    Text(
                        "మాల పూర్తయింది · Mala Complete",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // ── Tap Zone ─────────────────────────────────────────────────────
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                val ringSize: Dp = (maxWidth * 0.88f).coerceIn(260.dp, 340.dp)
                val circleSize: Dp = ringSize * 0.76f

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    val activeScale = if (count == 0 && !tapPaused) breathScale else 1f
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val tapPressScale by animateFloatAsState(
                        targetValue = if (isPressed) 0.93f else 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "tapPressScale",
                    )

                    // Progress ring with glow
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .size(ringSize)
                            .scale(glowScale),
                        color = progressColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 16.dp,
                        strokeCap = StrokeCap.Round,
                    )

                    // Tap circle
                    Box(
                        modifier = Modifier
                            .size(circleSize)
                            .scale(activeScale * tapPressScale * glowScale)
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            TempleGold.copy(alpha = glowAlpha * 0.6f),
                                            TempleGold.copy(alpha = glowAlpha * 0.2f),
                                            Color.Transparent,
                                        ),
                                        radius = size.minDimension / 2f * 1.1f,
                                    ),
                                )
                            }
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                            ) {
                                if (!tapPaused) {
                                    val nextCount = count + 1
                                    val nextBead = nextCount % 108
                                    when {
                                        nextBead == 0 -> { /* haptic handled by malaCompleteEvent LaunchedEffect */ }
                                        nextBead == 54 -> haptics.strongTap()
                                        nextBead == 27 || nextBead == 81 -> haptics.mediumTap()
                                        nextCount % 10 == 0 -> haptics.mediumTap()
                                        else -> haptics.lightTap()
                                    }
                                    viewModel.increment()
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                "ॐ",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TempleGold,
                            )
                            AnimatedVisibility(
                                visible = count > 0,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically(),
                            ) {
                                Text(
                                    formatElapsed(elapsedSeconds),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            AnimatedContent(
                                targetState = beadInMala,
                                transitionSpec = {
                                    slideInVertically { -it } + fadeIn() togetherWith
                                        slideOutVertically { it } + fadeOut()
                                },
                                label = "beadCount",
                            ) { displayCount ->
                                Text(
                                    "$displayCount",
                                    style = MaterialTheme.typography.displayLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
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

            // ── Undo Row ─────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = showUndo,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    TextButton(
                        onClick = { viewModel.undo() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        ),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("↩ undo", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // ── 3 Stat Cards ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                    value = "${if (beadInMala == 0 && count == 0) 108 else 108 - beadInMala}",
                    accentColor = SacredTurmeric,
                    modifier = Modifier.weight(1f),
                )
            }

            // ── Focus Toggle Pill ─────────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.25f)),
                onClick = { focusMode = !focusMode },
                modifier = Modifier.padding(vertical = 6.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        if (focusMode) Icons.Default.BarChart else Icons.Default.SelfImprovement,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TempleGold,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (focusMode) "Stats" else "Focus",
                        style = MaterialTheme.typography.labelSmall,
                        color = TempleGold,
                    )
                }
            }

            // ── Expandable Stats Panel ────────────────────────────────────────
            AnimatedVisibility(
                visible = !focusMode,
                enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Daily target card
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

                    // Streak & 7-day sparkline card
                    if (currentStreak > 0 || last7DaysActivity.any { it }) {
                        val milestoneLabel = when {
                            currentStreak >= 108 -> "108 రోజులు!"
                            currentStreak >= 40 -> "40 రోజులు!"
                            currentStreak >= 21 -> "21 రోజులు!"
                            currentStreak >= 7 -> "7 రోజులు!"
                            else -> null
                        }
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
                                    tint = if (currentStreak > 0) DeepVermillion
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(28.dp),
                                )
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        if (currentStreak > 0)
                                            "$currentStreak ${if (currentStreak == 1) "day" else "days"} streak"
                                        else "Start your streak today!",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentStreak > 0) DeepVermillion
                                        else MaterialTheme.colorScheme.onSurface,
                                    )
                                    if (longestStreak > currentStreak && longestStreak > 0) {
                                        Text(
                                            "Best: $longestStreak days  ·  $activeDaysCount total active",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    } else {
                                        Text(
                                            "$activeDaysCount total active days",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                                if (milestoneLabel != null) {
                                    Surface(
                                        shape = MaterialTheme.shapes.small,
                                        color = AuspiciousGreen.copy(alpha = 0.15f),
                                    ) {
                                        Text(
                                            milestoneLabel,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = AuspiciousGreen,
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                last7DaysActivity.forEachIndexed { i, active ->
                                    val dayEpoch = today.toEpochDays() - (6 - i)
                                    val dayOfWeek = LocalDate.fromEpochDays(dayEpoch)
                                        .dayOfWeek.name.take(3).lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                    val isToday = i == 6
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        active -> AuspiciousGreen.copy(alpha = 0.85f)
                                                        isToday -> TempleGold.copy(alpha = 0.25f)
                                                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                                    },
                                                ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            if (active) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp),
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(3.dp))
                                        Text(
                                            dayOfWeek,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = if (isToday) TempleGold
                                            else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Today's sessions
                    if (todaySessions.isNotEmpty()) {
                        SectionHeader(
                            titleTelugu = "నేటి సెషన్లు",
                            titleEnglish = "Today's Sessions",
                        )
                        todaySessions.forEach { session ->
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
                                            "${session.count} counts · ${session.malasCompleted} malas",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TempleGold,
                                        )
                                        Text(
                                            formatDuration(session.durationSeconds),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun formatElapsed(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        "$h:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
    } else {
        "$m:${s.toString().padStart(2, '0')}"
    }
}

private fun formatDuration(seconds: Long): String {
    return when {
        seconds <= 0L -> ""
        seconds < 60L -> "${seconds}s"
        else -> "${seconds / 60} min"
    }
}

@Composable
private fun JapaStatCard(
    label: String,
    value: String,
    accentColor: Color,
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
