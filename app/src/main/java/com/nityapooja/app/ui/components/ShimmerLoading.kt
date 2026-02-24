package com.nityapooja.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nityapooja.app.ui.theme.GoldShimmerEnd
import com.nityapooja.app.ui.theme.GoldShimmerHighlight
import com.nityapooja.app.ui.theme.GoldShimmerStart

@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier,
    itemCount: Int = 4,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val offset by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerOffset",
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            GoldShimmerStart.copy(alpha = 0.15f),
            GoldShimmerHighlight.copy(alpha = 0.3f),
            GoldShimmerStart.copy(alpha = 0.15f),
        ),
        start = Offset(offset, 0f),
        end = Offset(offset + 400f, 0f),
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(itemCount) {
            ShimmerCard(brush = brush)
        }
    }
}

@Composable
private fun ShimmerCard(brush: Brush) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(brush)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ShimmerLine(brush, width = 0.6f, height = 16.dp)
        ShimmerLine(brush, width = 0.4f, height = 12.dp)
    }
}

@Composable
private fun ShimmerLine(brush: Brush, width: Float, height: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth(width)
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(brush),
    )
}
