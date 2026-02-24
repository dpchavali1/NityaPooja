package com.nityapooja.app.ui.virtualpooja

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nityapooja.app.data.local.entity.DeityEntity
import com.nityapooja.app.ui.components.resolveDeityColor

/**
 * Central altar/mandir area displaying the deity image
 * with decorative arch background and all offering overlays.
 */
@Composable
fun MandirAltarArea(
    deity: DeityEntity?,
    offerings: Map<PoojaItem, OfferingState>,
    abhishekamType: AbhishekamType,
    floatingPetals: List<FloatingPetal>,
    smokeParticles: List<SmokeParticle>,
    modifier: Modifier = Modifier,
) {
    val deityColor = resolveDeityColor(deity?.colorTheme)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Layer 1: Pulsing arch background
        AltarBackground(deityColor = deityColor, modifier = Modifier.matchParentSize())

        // Layer 2: Deepam glow only (behind deity — radial glow is semi-transparent)
        DeepamGlowBackground(
            isDone = offerings[PoojaItem.DEEPAM]?.isDone == true,
            modifier = Modifier.matchParentSize(),
        )

        // Layer 3: Deity image
        deity?.let { DeityImage(it) }

        // Layer 4: Deepam lamp (in front of deity so lamp is visible at bottom)
        DeepamLampOverlay(
            isDone = offerings[PoojaItem.DEEPAM]?.isDone == true,
            modifier = Modifier.matchParentSize(),
        )

        // Layer 5: Kumkum tilak (on top of deity image, at forehead)
        KumkumTilakOverlay(
            isDone = offerings[PoojaItem.KUMKUM]?.isDone == true,
            modifier = Modifier.matchParentSize(),
        )

        // Layer 6: Flower petals
        FloatingPetalsOverlay(
            petals = floatingPetals,
            modifier = Modifier.matchParentSize(),
        )

        // Layer 7: Smoke particles
        SmokeOverlay(
            particles = smokeParticles,
            modifier = Modifier.matchParentSize(),
        )

        // Layer 8: Abhishekam water/milk bathing
        AbhishekamOverlay(
            isAnimating = offerings[PoojaItem.ABHISHEKAM]?.isAnimating == true,
            abhishekamType = abhishekamType,
            modifier = Modifier.matchParentSize(),
        )

        // Layer 9: Harathi arc
        HarathiArcOverlay(
            isAnimating = offerings[PoojaItem.HARATHI]?.isAnimating == true,
            modifier = Modifier.matchParentSize(),
        )

        // Layer 10: Naivedyam at feet
        NaivedyamOverlay(
            isDone = offerings[PoojaItem.NAIVEDYAM]?.isDone == true,
            modifier = Modifier.matchParentSize(),
        )
    }
}

@Composable
private fun DeityImage(deity: DeityEntity) {
    val context = LocalContext.current
    val imageResName = deity.imageResName
    if (imageResName != null) {
        val resId = context.resources.getIdentifier(imageResName, "drawable", context.packageName)
        if (resId != 0) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = deity.nameTelugu,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
            )
            return
        }
    }
    // Fallback: no image found — show nothing (altar stays decorated)
}

@Composable
private fun AltarBackground(
    deityColor: Color,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "altarBg")
    val bgAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bgAlpha",
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Radial gradient centered on deity area
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    deityColor.copy(alpha = bgAlpha),
                    deityColor.copy(alpha = bgAlpha * 0.3f),
                    Color.Transparent,
                ),
                center = Offset(w / 2f, h * 0.4f),
                radius = w * 0.65f,
            ),
            size = size,
        )

        // Temple arch outline
        val archWidth = w * 0.72f
        val archLeft = (w - archWidth) / 2f
        val archRight = archLeft + archWidth
        val archTop = h * 0.03f
        val archBottom = h * 0.92f
        val archRadius = archWidth / 2f

        val archPath = Path().apply {
            moveTo(archLeft, archBottom)
            lineTo(archLeft, archTop + archRadius)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(
                    archLeft, archTop, archRight, archTop + archRadius * 2
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false,
            )
            lineTo(archRight, archBottom)
        }

        // Filled arch (very subtle)
        drawPath(
            path = archPath,
            color = deityColor.copy(alpha = 0.06f),
        )

        // Arch border
        drawPath(
            path = archPath,
            color = deityColor.copy(alpha = 0.2f),
            style = Stroke(width = 2f),
        )
    }
}
