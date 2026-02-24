package com.nityapooja.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DeityAvatar(
    nameTelugu: String,
    nameEnglish: String,
    deityColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    showLabel: Boolean = true,
    imageResName: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(16.dp)
    val context = LocalContext.current

    // Resolve drawable resource ID from name
    val imageResId = imageResName?.let {
        context.resources.getIdentifier(it, "drawable", context.packageName)
    }?.takeIf { it != 0 }

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .shadow(6.dp, shape, ambientColor = deityColor.copy(alpha = 0.3f))
                .border(2.5.dp, deityColor.copy(alpha = 0.7f), shape)
                .clip(shape)
                .background(deityColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            if (imageResId != null) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = nameEnglish,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                // Fallback: text initials
                Text(
                    text = nameTelugu.take(2),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = deityColor,
                )
            }
        }
        if (showLabel) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = nameEnglish
                    .removePrefix("Lord ")
                    .removePrefix("Goddess ")
                    .take(12),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}
