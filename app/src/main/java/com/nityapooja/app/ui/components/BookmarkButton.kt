package com.nityapooja.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import com.nityapooja.app.ui.theme.TempleGold

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var justToggled by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (justToggled) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        finishedListener = { justToggled = false },
        label = "bookmarkScale",
    )
    val tint by animateColorAsState(
        targetValue = if (isBookmarked) TempleGold else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "bookmarkColor",
    )

    IconButton(
        onClick = {
            justToggled = true
            onToggle()
        },
        modifier = modifier.scale(scale),
    ) {
        Icon(
            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = if (isBookmarked) "Remove Bookmark" else "Add Bookmark",
            tint = tint,
        )
    }
}
