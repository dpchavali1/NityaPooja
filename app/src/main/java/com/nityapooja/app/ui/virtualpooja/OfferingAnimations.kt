package com.nityapooja.app.ui.virtualpooja

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.nityapooja.app.ui.theme.TempleGold
import com.nityapooja.app.ui.theme.TempleGoldLight
import kotlin.math.cos
import kotlin.math.sin

// ═══════════════════════════════════════════════════════════
// 1. Deepam — Oil lamp with flickering flame (persistent)
// ═══════════════════════════════════════════════════════════

/**
 * Radial golden glow behind the deity (Layer 2 — behind deity image).
 */
@Composable
fun DeepamGlowBackground(
    isDone: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!isDone) return

    val infiniteTransition = rememberInfiniteTransition(label = "deepamGlowBg")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    TempleGold.copy(alpha = glowAlpha),
                    TempleGoldLight.copy(alpha = glowAlpha * 0.3f),
                    Color.Transparent,
                ),
                center = Offset(w / 2f, h * 0.4f),
                radius = w * 0.5f,
            ),
        )
    }
}

/**
 * Deepam oil lamp with flickering flame (Layer 4 — in front of deity image).
 */
@Composable
fun DeepamLampOverlay(
    isDone: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!isDone) return

    val infiniteTransition = rememberInfiniteTransition(label = "deepamLamp")

    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "flameScale",
    )
    val flameSway by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "flameSway",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // --- Deepam lamp at bottom center ---
        val lampCx = w / 2f
        val lampBaseY = h * 0.88f

        // Oil reservoir (lamp body) — rounded trapezoid shape
        val lampW = 50f
        val lampH = 18f
        val lampPath = Path().apply {
            moveTo(lampCx - lampW / 2f, lampBaseY)
            lineTo(lampCx - lampW * 0.35f, lampBaseY - lampH)
            lineTo(lampCx + lampW * 0.35f, lampBaseY - lampH)
            lineTo(lampCx + lampW / 2f, lampBaseY)
            close()
        }
        drawPath(lampPath, color = Color(0xFFCD7F32)) // Bronze

        // Lamp rim
        drawPath(lampPath, color = Color(0xFFDDA15E), style = Stroke(width = 2f))

        // Lamp stand (small stem + base)
        drawRect(
            color = Color(0xFFCD7F32),
            topLeft = Offset(lampCx - 4f, lampBaseY),
            size = Size(8f, 10f),
        )
        drawOval(
            color = Color(0xFFCD7F32),
            topLeft = Offset(lampCx - 16f, lampBaseY + 8f),
            size = Size(32f, 8f),
        )

        // Wick (small white line)
        drawLine(
            color = Color(0xFFDDD8C4),
            start = Offset(lampCx, lampBaseY - lampH),
            end = Offset(lampCx, lampBaseY - lampH - 6f),
            strokeWidth = 2f,
        )

        // --- Flickering flame ---
        val flameBaseY = lampBaseY - lampH - 6f
        val flameH = 28f * flameScale
        val flameW = 12f * flameScale

        // Outer flame (orange-yellow)
        val outerFlamePath = Path().apply {
            moveTo(lampCx - flameW / 2f + flameSway * 0.5f, flameBaseY)
            quadraticBezierTo(
                lampCx - flameW * 0.6f + flameSway, flameBaseY - flameH * 0.5f,
                lampCx + flameSway * 0.7f, flameBaseY - flameH,
            )
            quadraticBezierTo(
                lampCx + flameW * 0.6f + flameSway, flameBaseY - flameH * 0.5f,
                lampCx + flameW / 2f + flameSway * 0.5f, flameBaseY,
            )
            close()
        }
        drawPath(outerFlamePath, color = Color(0xFFFFA726)) // Orange

        // Inner flame (bright yellow)
        val innerFlameH = flameH * 0.6f
        val innerFlameW = flameW * 0.5f
        val innerFlamePath = Path().apply {
            moveTo(lampCx - innerFlameW / 2f + flameSway * 0.3f, flameBaseY)
            quadraticBezierTo(
                lampCx - innerFlameW * 0.4f + flameSway * 0.5f, flameBaseY - innerFlameH * 0.5f,
                lampCx + flameSway * 0.4f, flameBaseY - innerFlameH,
            )
            quadraticBezierTo(
                lampCx + innerFlameW * 0.4f + flameSway * 0.5f, flameBaseY - innerFlameH * 0.5f,
                lampCx + innerFlameW / 2f + flameSway * 0.3f, flameBaseY,
            )
            close()
        }
        drawPath(innerFlamePath, color = Color(0xFFFFEB3B)) // Yellow

        // Core (white-hot center)
        val coreH = flameH * 0.25f
        val coreW = flameW * 0.2f
        drawOval(
            color = Color(0xCCFFFFFF),
            topLeft = Offset(lampCx - coreW / 2f + flameSway * 0.2f, flameBaseY - coreH),
            size = Size(coreW, coreH),
        )

        // Small glow around flame
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x40FFA726),
                    Color.Transparent,
                ),
                center = Offset(lampCx, flameBaseY - flameH * 0.5f),
                radius = 40f,
            ),
        )
    }
}

// ═══════════════════════════════════════════════════════════
// 2. Pushpam — Flower petals falling from top
// ═══════════════════════════════════════════════════════════

private val PETAL_EMOJIS = listOf("🌸", "🌺", "🌼", "💐")

@Composable
fun FloatingPetalsOverlay(
    petals: List<FloatingPetal>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        petals.forEach { petal ->
            FloatingPetalItem(petal = petal)
        }
    }
}

@Composable
private fun FloatingPetalItem(petal: FloatingPetal) {
    var started by remember(petal.id) { mutableStateOf(false) }

    val targetY = 1f
    val offsetYFraction by animateFloatAsState(
        targetValue = if (started) targetY else -0.05f,
        animationSpec = tween(
            durationMillis = 2500 + (petal.id % 4) * 300,
            easing = LinearEasing,
        ),
        label = "petalFall_${petal.id}",
    )
    val rotation by animateFloatAsState(
        targetValue = if (started) petal.rotation + 180f else petal.rotation,
        animationSpec = tween(2500, easing = LinearEasing),
        label = "petalRotate_${petal.id}",
    )

    LaunchedEffect(petal.id) { started = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        Text(
            text = PETAL_EMOJIS[petal.colorIndex % PETAL_EMOJIS.size],
            fontSize = (18 * petal.sizeFactor).sp,
            modifier = Modifier
                .offset(
                    x = (petal.startX * 280).dp,
                    y = (offsetYFraction * 400).dp,
                )
                .rotate(rotation),
        )
    }
}

// ═══════════════════════════════════════════════════════════
// 3. Naivedyam — Offerings spring up at deity feet
// ═══════════════════════════════════════════════════════════

@Composable
fun NaivedyamOverlay(
    isDone: Boolean,
    modifier: Modifier = Modifier,
) {
    val offsetY by animateFloatAsState(
        targetValue = if (isDone) 0f else 60f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "naivedyamOffset",
    )
    val alpha by animateFloatAsState(
        targetValue = if (isDone) 1f else 0f,
        animationSpec = tween(300),
        label = "naivedyamAlpha",
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Row(
            modifier = Modifier
                .offset(y = offsetY.dp)
                .alpha(alpha)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("🥥", "🍎", "🍌", "🍇").forEach { emoji ->
                Text(emoji, fontSize = 24.sp)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 4. Dhoop — Two incense sticks with smoke rising from bottom to top
// ═══════════════════════════════════════════════════════════

@Composable
fun SmokeOverlay(
    particles: List<SmokeParticle>,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dhoopSmoke")

    // Smoke wave offsets for organic movement
    val smokeWave1 by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "smokeWave1",
    )
    val smokeWave2 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "smokeWave2",
    )
    val smokeWave3 by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "smokeWave3",
    )
    // Subtle glow at stick tips
    val tipGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "tipGlow",
    )

    val hasDhoop = particles.isNotEmpty()

    Canvas(modifier = modifier.fillMaxSize()) {
        if (!hasDhoop) return@Canvas

        val w = size.width
        val h = size.height

        // Two stick positions — slightly spread apart and angled
        val stickPositions = listOf(
            Triple(w * 0.35f, w * 0.33f, -5f),  // left stick: bottomX, topX, angle hint
            Triple(w * 0.65f, w * 0.67f, 5f),   // right stick: bottomX, topX, angle hint
        )

        for ((idx, stick) in stickPositions.withIndex()) {
            val (bottomX, topX, _) = stick
            val stickBottomY = h * 0.92f
            val stickTopY = h * 0.55f
            val stickWidth = 3f

            // Stick body (bamboo color)
            drawLine(
                color = Color(0xFF8D6E63),
                start = Offset(bottomX, stickBottomY),
                end = Offset(topX, stickTopY),
                strokeWidth = stickWidth,
                cap = StrokeCap.Round,
            )

            // Incense coating on upper 40% (darker brown)
            val coatEndY = stickTopY + (stickBottomY - stickTopY) * 0.4f
            val coatEndX = topX + (bottomX - topX) * 0.4f
            drawLine(
                color = Color(0xFF5D4037),
                start = Offset(topX, stickTopY),
                end = Offset(coatEndX, coatEndY),
                strokeWidth = stickWidth + 1.5f,
                cap = StrokeCap.Round,
            )

            // Glowing ember at tip
            drawCircle(
                color = Color(0xFFFF6D00).copy(alpha = tipGlow),
                center = Offset(topX, stickTopY),
                radius = 4f,
            )
            drawCircle(
                color = Color(0xFFFFAB00).copy(alpha = tipGlow * 0.5f),
                center = Offset(topX, stickTopY),
                radius = 8f,
            )

            // Smoke wisps rising from each stick tip
            val smokeLayers = 8
            for (i in 0 until smokeLayers) {
                val fraction = i.toFloat() / smokeLayers
                val smokeY = stickTopY - fraction * (stickTopY - h * 0.05f)
                val smokeAlpha = (1f - fraction) * 0.22f
                val smokeRadius = 8f + fraction * 32f
                val waveOffset = when {
                    idx == 0 && i % 2 == 0 -> smokeWave1
                    idx == 0 -> smokeWave2
                    i % 2 == 0 -> smokeWave3
                    else -> smokeWave1
                }
                val xDrift = waveOffset * fraction * 3f

                drawCircle(
                    color = Color.White.copy(alpha = smokeAlpha),
                    center = Offset(topX + xDrift, smokeY),
                    radius = smokeRadius,
                )
            }
        }

        // Merged smoke plume where the two streams meet at the top
        val mergeY = h * 0.15f
        val mergeCx = w * 0.5f
        for (i in 0..4) {
            val fraction = i / 4f
            val y = mergeY - fraction * mergeY * 0.5f
            val alpha = (1f - fraction) * 0.15f
            val radius = 25f + fraction * 30f
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                center = Offset(mergeCx + smokeWave2 * fraction * 2f, y),
                radius = radius,
            )
        }
    }

    // Also draw the particle-based smoke for variety
    Box(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            SmokeParticleItem(particle = particle)
        }
    }
}

@Composable
private fun SmokeParticleItem(particle: SmokeParticle) {
    var started by remember(particle.id) { mutableStateOf(false) }

    val offsetYFraction by animateFloatAsState(
        targetValue = if (started) -0.5f else 0.1f,
        animationSpec = tween(3500, easing = LinearOutSlowInEasing),
        label = "smokeRise_${particle.id}",
    )
    val alpha by animateFloatAsState(
        targetValue = if (started) 0f else 0.3f,
        animationSpec = tween(3500),
        label = "smokeFade_${particle.id}",
    )
    val scale by animateFloatAsState(
        targetValue = if (started) 3.5f else 0.8f,
        animationSpec = tween(3500),
        label = "smokeScale_${particle.id}",
    )

    LaunchedEffect(particle.id) { started = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        Box(
            modifier = Modifier
                .offset(
                    x = ((particle.startX * 0.4f + 0.15f + particle.driftX * if (started) 1f else 0f) * 280).dp,
                    y = (offsetYFraction * 400 + 200).dp,
                )
                .scale(scale)
                .alpha(alpha)
                .size(16.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.4f)),
        )
    }
}

// ═══════════════════════════════════════════════════════════
// 5. Abhishekam — Heavy bathing from tilted kalash
//    Supports water (blue/transparent) and milk (white/creamy)
// ═══════════════════════════════════════════════════════════

@Composable
fun AbhishekamOverlay(
    isAnimating: Boolean,
    abhishekamType: AbhishekamType = AbhishekamType.WATER,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isAnimating,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(400)),
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "abhishekam")

        val streamWobble by infiniteTransition.animateFloat(
            initialValue = -2f, targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ), label = "streamWobble",
        )
        val dropProgress by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ), label = "dropCascade",
        )
        val splashPhase by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(700, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart,
            ), label = "splashPhase",
        )
        val vesselRock by infiniteTransition.animateFloat(
            initialValue = -2f, targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ), label = "vesselRock",
        )
        val flowPulse by infiniteTransition.animateFloat(
            initialValue = 0.9f, targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(350, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ), label = "flowPulse",
        )
        val sheetWave by infiniteTransition.animateFloat(
            initialValue = -4f, targetValue = 4f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ), label = "sheetWave",
        )

        Canvas(modifier = modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val centerX = w / 2f

            // --- Color scheme based on abhishekam type ---
            val isMilk = abhishekamType == AbhishekamType.MILK

            // Water: transparent blue, glassy, light
            // Milk: opaque creamy white, thick, rich
            val streamColor = if (isMilk) Color(0xDDF5F0E0) else Color(0x9944B8E8)
            val streamDark = if (isMilk) Color(0xCCE8DCC8) else Color(0x882196F3)
            val streamLight = if (isMilk) Color(0xAAFAF6ED) else Color(0x6690CAF9)
            val cascadeColor = if (isMilk) Color(0x88F5F0E0) else Color(0x5581D4FA)
            val sheenColor = if (isMilk) Color(0x55FFFEF7) else Color(0x4481D4FA)
            val highlightColor = if (isMilk) Color(0x88FFFFFF) else Color(0x66FFFFFF)
            val poolColor = if (isMilk) Color(0x66F5F0E0) else Color(0x5581D4FA)

            // --- Large tilted kalash at top-right ---
            val vesselCx = centerX + 45f + vesselRock
            val vesselTopY = h * 0.005f
            val vesselW = 50f
            val vesselH = 32f

            val kalashPath = Path().apply {
                moveTo(vesselCx - vesselW * 0.2f, vesselTopY + vesselH)
                quadraticBezierTo(
                    vesselCx - vesselW * 0.6f, vesselTopY + vesselH * 0.5f,
                    vesselCx - vesselW * 0.35f, vesselTopY + 3f,
                )
                lineTo(vesselCx + vesselW * 0.35f, vesselTopY + 1f)
                quadraticBezierTo(
                    vesselCx + vesselW * 0.6f, vesselTopY + vesselH * 0.5f,
                    vesselCx + vesselW * 0.2f, vesselTopY + vesselH,
                )
                close()
            }
            drawPath(kalashPath, color = Color(0xFFD4A017))
            drawPath(kalashPath, color = Color(0xFFE8C547), style = Stroke(width = 2f))
            drawOval(
                color = Color(0xFFE8C547),
                topLeft = Offset(vesselCx - 12f, vesselTopY),
                size = Size(24f, 9f),
            )
            drawLine(
                color = Color(0xFFB8860B),
                start = Offset(vesselCx - vesselW * 0.38f, vesselTopY + vesselH * 0.55f),
                end = Offset(vesselCx + vesselW * 0.38f, vesselTopY + vesselH * 0.55f),
                strokeWidth = 2.5f,
            )

            // Pour spout
            val spoutX = vesselCx - vesselW * 0.35f
            val spoutY = vesselTopY + 5f

            // --- THICK main pour stream (parabolic arc) ---
            val pourEndY = h * 0.28f  // hits top of deity head
            val pourEndX = centerX + streamWobble
            val streamW = 22f * flowPulse  // THICK stream

            val streamPath = Path().apply {
                for (i in 0..30) {
                    val frac = i / 30f
                    val x = spoutX + (pourEndX - spoutX) * frac + streamWobble * frac * 0.5f
                    val y = spoutY + (pourEndY - spoutY) * (frac * 0.5f + frac * frac * 0.5f)
                    val widthHere = streamW * (0.7f + frac * 0.3f)
                    if (i == 0) moveTo(x - widthHere / 2f, y)
                    else lineTo(x - widthHere / 2f, y)
                }
                for (i in 30 downTo 0) {
                    val frac = i / 30f
                    val x = spoutX + (pourEndX - spoutX) * frac + streamWobble * frac * 0.5f
                    val y = spoutY + (pourEndY - spoutY) * (frac * 0.5f + frac * frac * 0.5f)
                    val widthHere = streamW * (0.7f + frac * 0.3f)
                    lineTo(x + widthHere / 2f, y)
                }
                close()
            }
            drawPath(streamPath, color = streamColor)
            // For water: add translucent white sheen; for milk: add creamy overlay
            drawPath(streamPath, color = highlightColor.copy(alpha = if (isMilk) 0.15f else 0.1f))

            // Bright highlight through stream center
            for (i in 0..25) {
                val frac = i / 25f
                val x = spoutX + (pourEndX - spoutX) * frac + streamWobble * frac * 0.5f
                val y = spoutY + (pourEndY - spoutY) * (frac * 0.5f + frac * frac * 0.5f)
                drawCircle(
                    color = highlightColor.copy(alpha = 0.5f - frac * 0.25f),
                    center = Offset(x, y),
                    radius = if (isMilk) 3f else 2.5f,
                )
            }

            // --- LIQUID CASCADING DOWN THE DEITY (bathing effect) ---
            val sheetTopY = pourEndY - 5f
            val sheetBottomY = h * 0.85f
            val deityHalfW = w * 0.22f

            // Left cascade
            val leftCascade = Path().apply {
                moveTo(centerX - 8f, sheetTopY)
                for (i in 0..20) {
                    val frac = i / 20f
                    val y = sheetTopY + (sheetBottomY - sheetTopY) * frac
                    val spread = deityHalfW * (0.3f + frac * 0.7f)
                    val wave = sheetWave * frac * 1.5f
                    lineTo(centerX - spread + wave, y)
                }
                for (i in 20 downTo 0) {
                    val frac = i / 20f
                    val y = sheetTopY + (sheetBottomY - sheetTopY) * frac
                    val spread = deityHalfW * (0.1f + frac * 0.3f)
                    lineTo(centerX - spread, y)
                }
                close()
            }
            drawPath(leftCascade, color = cascadeColor)

            // Right cascade
            val rightCascade = Path().apply {
                moveTo(centerX + 8f, sheetTopY)
                for (i in 0..20) {
                    val frac = i / 20f
                    val y = sheetTopY + (sheetBottomY - sheetTopY) * frac
                    val spread = deityHalfW * (0.3f + frac * 0.7f)
                    val wave = -sheetWave * frac * 1.5f
                    lineTo(centerX + spread + wave, y)
                }
                for (i in 20 downTo 0) {
                    val frac = i / 20f
                    val y = sheetTopY + (sheetBottomY - sheetTopY) * frac
                    val spread = deityHalfW * (0.1f + frac * 0.3f)
                    lineTo(centerX + spread, y)
                }
                close()
            }
            drawPath(rightCascade, color = cascadeColor)

            // Center waterfall sheet (thicker, more visible)
            val centerSheet = Path().apply {
                moveTo(centerX - 15f, sheetTopY)
                for (i in 0..20) {
                    val frac = i / 20f
                    val y = sheetTopY + (sheetBottomY - sheetTopY) * frac
                    val spreadW = 15f + frac * 25f
                    lineTo(centerX - spreadW + streamWobble * frac, y)
                }
                for (i in 20 downTo 0) {
                    val frac = i / 20f
                    val y = sheetTopY + (sheetBottomY - sheetTopY) * frac
                    val spreadW = 15f + frac * 25f
                    lineTo(centerX + spreadW + streamWobble * frac, y)
                }
                close()
            }
            drawPath(centerSheet, color = streamColor.copy(alpha = if (isMilk) 0.35f else 0.25f))

            // --- Dripping streams running down deity ---
            for (d in 0 until 9) {
                val dripPhase = (dropProgress + d * 0.11f) % 1f
                val offsetFromCenter = (d - 4) * 18f + sheetWave * 0.3f
                val dripX = centerX + offsetFromCenter
                val dripStartY2 = sheetTopY + 20f + d * 8f
                val dripY = dripStartY2 + (sheetBottomY - dripStartY2) * dripPhase
                val dripAlpha = (1f - dripPhase) * (if (isMilk) 0.55f else 0.45f)
                val dripLen = 12f + dripPhase * 8f

                drawOval(
                    color = streamDark.copy(alpha = dripAlpha),
                    topLeft = Offset(dripX - 2f, dripY),
                    size = Size(4f, dripLen),
                )
            }

            // --- Splash at deity head ---
            val splashY = pourEndY + 3f
            for (i in 0 until 10) {
                val angle = (i.toFloat() / 10f) * 360f
                val radians = Math.toRadians(angle.toDouble()).toFloat()
                val dist = 10f + splashPhase * 28f
                val splashAlpha = (1f - splashPhase) * 0.45f

                val sx = centerX + cos(radians) * dist + streamWobble
                val sy = splashY + sin(radians) * dist * 0.25f

                drawCircle(
                    color = streamLight.copy(alpha = splashAlpha),
                    center = Offset(sx, sy),
                    radius = 2.5f - splashPhase * 1f,
                )
            }

            // --- Side droplets from stream ---
            for (i in 0 until 12) {
                val phase = (dropProgress + i * 0.08f) % 1f
                val frac = phase
                val baseX = spoutX + (pourEndX - spoutX) * frac
                val baseY = spoutY + (pourEndY - spoutY) * (frac * 0.5f + frac * frac * 0.5f)
                val dropAlpha = (1f - frac) * (if (isMilk) 0.55f else 0.45f)
                val sideOff = if (i % 2 == 0) -(streamW * 0.6f + 5f) else (streamW * 0.6f + 5f)
                val dropSize = 2f + frac * 3f

                drawOval(
                    color = streamDark.copy(alpha = dropAlpha),
                    topLeft = Offset(baseX + sideOff, baseY),
                    size = Size(dropSize, dropSize * 2f),
                )
            }

            // --- Wet sheen on deity ---
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        sheenColor.copy(alpha = if (isMilk) 0.25f else 0.2f),
                        sheenColor.copy(alpha = 0.08f),
                        Color.Transparent,
                    ),
                    center = Offset(centerX, h * 0.45f),
                    radius = w * 0.28f,
                ),
                topLeft = Offset(centerX - w * 0.28f, h * 0.2f),
                size = Size(w * 0.56f, h * 0.5f),
            )

            // --- Pool at bottom ---
            drawOval(
                color = poolColor,
                topLeft = Offset(centerX - w * 0.2f, sheetBottomY),
                size = Size(w * 0.4f, 16f),
            )
            drawOval(
                color = streamColor.copy(alpha = if (isMilk) 0.25f else 0.15f),
                topLeft = Offset(centerX - w * 0.15f, sheetBottomY + 4f),
                size = Size(w * 0.3f, 10f),
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 6. Harathi — Spoon/plate with big flame, semicircular sweep
//    in front of deity from mid to top of head (toggle on/off)
// ═══════════════════════════════════════════════════════════

@Composable
fun HarathiArcOverlay(
    isAnimating: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isAnimating,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(600)),
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "harathi")

        // Full circular motion in front of deity (top to bottom, continuous)
        val orbitAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(5000, easing = LinearEasing),
            ),
            label = "harathiOrbit",
        )

        // Flame flicker
        val flameFlicker by infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(350, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "harathiFlameFlicker",
        )

        // Flame sway
        val flameSway by infiniteTransition.animateFloat(
            initialValue = -3f,
            targetValue = 3f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "flameSway",
        )

        // Trail glow
        val trailAlpha by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(600),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "harathiTrail",
        )

        Canvas(modifier = modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val centerX = w / 2f
            // Orbit center at mid-deity
            val centerY = h * 0.42f

            // Vertical ellipse — narrower side-to-side, taller top-to-bottom
            // so the spoon sweeps from top of head down to mid/bottom in front
            val orbitRadiusX = w * 0.18f
            val orbitRadiusY = h * 0.22f

            val angleRad = Math.toRadians(orbitAngle.toDouble()).toFloat()

            // Spoon orbits in a vertical circle in front of the deity
            val spoonX = centerX + cos(angleRad) * orbitRadiusX
            val spoonY = centerY + sin(angleRad) * orbitRadiusY

            // --- Soft golden trail behind the spoon ---
            val trailSegments = 18
            for (i in 0 until trailSegments) {
                val trailAngle = orbitAngle - i * 5f
                val trailRad = Math.toRadians(trailAngle.toDouble()).toFloat()
                val tx = centerX + cos(trailRad) * orbitRadiusX
                val ty = centerY + sin(trailRad) * orbitRadiusY
                val segAlpha = trailAlpha * (1f - i.toFloat() / trailSegments)

                drawCircle(
                    color = TempleGold.copy(alpha = segAlpha * 0.4f),
                    center = Offset(tx, ty),
                    radius = 8f - i * 0.35f,
                )
            }

            // --- Spoon/plate shape (elongated oval with handle) ---
            val spoonW = 30f   // bowl width
            val spoonH = 22f   // bowl height

            // Handle points downward (towards user), always below the bowl
            val handleAngle = angleRad + 1.57f // perpendicular outward from orbit center
            val handleLen = 28f
            val handleEndX = spoonX + cos(handleAngle) * handleLen
            val handleEndY = spoonY + sin(handleAngle) * handleLen

            // Draw handle (bronze rod)
            drawLine(
                color = Color(0xFFB8860B),
                start = Offset(spoonX, spoonY),
                end = Offset(handleEndX, handleEndY),
                strokeWidth = 4f,
                cap = StrokeCap.Round,
            )
            // Handle highlight
            drawLine(
                color = Color(0xFFDDA15E),
                start = Offset(spoonX, spoonY),
                end = Offset(handleEndX, handleEndY),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round,
            )

            // Spoon bowl shadow
            drawOval(
                color = Color.Black.copy(alpha = 0.12f),
                topLeft = Offset(spoonX - spoonW / 2f - 1f, spoonY - spoonH / 2f + 3f),
                size = Size(spoonW + 2f, spoonH + 2f),
            )

            // Spoon bowl (bronze plate)
            drawOval(
                color = Color(0xFFCD7F32),
                topLeft = Offset(spoonX - spoonW / 2f, spoonY - spoonH / 2f),
                size = Size(spoonW, spoonH),
            )
            // Bowl rim
            drawOval(
                color = Color(0xFFDDA15E),
                topLeft = Offset(spoonX - spoonW / 2f, spoonY - spoonH / 2f),
                size = Size(spoonW, spoonH),
                style = Stroke(width = 2f),
            )
            // Inner bowl detail
            drawOval(
                color = Color(0xFFB8860B).copy(alpha = 0.5f),
                topLeft = Offset(spoonX - spoonW * 0.3f, spoonY - spoonH * 0.3f),
                size = Size(spoonW * 0.6f, spoonH * 0.6f),
                style = Stroke(width = 1f),
            )

            // --- Big camphor flame on the spoon ---
            val fx = spoonX + flameSway * 0.3f
            val fy = spoonY - spoonH * 0.15f  // flame sits on top of bowl

            // Outer flame (large, orange-red)
            val fh = 32f * flameFlicker
            val fw = 16f * flameFlicker
            val outerPath = Path().apply {
                moveTo(fx - fw / 2f, fy)
                quadraticBezierTo(fx - fw * 0.6f + flameSway, fy - fh * 0.5f, fx + flameSway * 0.5f, fy - fh)
                quadraticBezierTo(fx + fw * 0.6f + flameSway, fy - fh * 0.5f, fx + fw / 2f, fy)
                close()
            }
            drawPath(outerPath, color = Color(0xFFFFA726))

            // Middle flame (bright orange)
            val mfh = fh * 0.7f
            val mfw = fw * 0.65f
            val middlePath = Path().apply {
                moveTo(fx - mfw / 2f, fy)
                quadraticBezierTo(fx - mfw * 0.5f + flameSway * 0.7f, fy - mfh * 0.5f, fx + flameSway * 0.3f, fy - mfh)
                quadraticBezierTo(fx + mfw * 0.5f + flameSway * 0.7f, fy - mfh * 0.5f, fx + mfw / 2f, fy)
                close()
            }
            drawPath(middlePath, color = Color(0xFFFFCC02))

            // Inner flame (bright yellow)
            val ifh = fh * 0.4f
            val ifw = fw * 0.35f
            val innerPath = Path().apply {
                moveTo(fx - ifw / 2f, fy)
                quadraticBezierTo(fx - ifw * 0.4f + flameSway * 0.4f, fy - ifh * 0.5f, fx + flameSway * 0.2f, fy - ifh)
                quadraticBezierTo(fx + ifw * 0.4f + flameSway * 0.4f, fy - ifh * 0.5f, fx + ifw / 2f, fy)
                close()
            }
            drawPath(innerPath, color = Color(0xFFFFEB3B))

            // White-hot core
            drawOval(
                color = Color(0xCCFFFFFF),
                topLeft = Offset(fx - 3f, fy - fh * 0.15f),
                size = Size(6f, fh * 0.12f),
            )

            // Warm glow around flame
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x50FFA726),
                        Color(0x20FFCC02),
                        Color.Transparent,
                    ),
                    center = Offset(fx, fy - fh * 0.3f),
                    radius = 55f,
                ),
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 7. Kumkum — Tilak dot at forehead (persistent)
// ═══════════════════════════════════════════════════════════

@Composable
fun KumkumTilakOverlay(
    isDone: Boolean,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (isDone) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "tilakScale",
    )

    // Deity image is 200dp centered in the Box.
    // Forehead is ~70dp above center (30dp from top of 200dp image).
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .offset(y = (-50).dp) // forehead/between eyebrows area
                .size(12.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(Color(0xFFE53935)), // Vermillion red
        )
    }
}
