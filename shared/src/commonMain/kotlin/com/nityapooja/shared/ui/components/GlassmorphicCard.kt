package com.nityapooja.shared.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nityapooja.shared.ui.theme.LocalExtendedColors

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    accentColor: Color? = null,
    cornerRadius: Dp = 20.dp,
    contentPadding: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val extended = LocalExtendedColors.current
    val borderColor = accentColor?.copy(alpha = 0.3f) ?: extended.glassBorder
    val surfaceColor = extended.cardSurface

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = accentColor?.copy(alpha = 0.1f) ?: Color.Black.copy(alpha = 0.08f),
                spotColor = accentColor?.copy(alpha = 0.15f) ?: Color.Black.copy(alpha = 0.05f),
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = surfaceColor,
        border = BorderStroke(1.dp, borderColor),
        onClick = onClick ?: {},
        enabled = onClick != null,
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}
