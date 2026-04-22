package com.nityapooja.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

/**
 * Drop-in replacement for DeityAvatar that shows the user's custom photo (if set)
 * instead of the bundled drawable. Falls back to DeityAvatar when no custom photo exists.
 *
 * Use this everywhere DeityAvatar is used — it is observationally identical to DeityAvatar
 * unless a custom photo has been saved via DeityDetailScreen.
 */
@Composable
fun SmartDeityImage(
    deityId: Int,
    nameTelugu: String,
    nameEnglish: String,
    deityColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    showLabel: Boolean = true,
    imageResName: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val prefs = koinInject<UserPreferencesManager>()
    var customPath by remember(deityId) { mutableStateOf<String?>(null) }
    LaunchedEffect(deityId) {
        customPath = prefs.getCustomDeityImagePath(deityId)
    }

    if (customPath != null) {
        val shape = RoundedCornerShape(16.dp)
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
                AsyncImage(
                    model = customPath,
                    contentDescription = nameEnglish,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
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
    } else {
        DeityAvatar(
            nameTelugu = nameTelugu,
            nameEnglish = nameEnglish,
            deityColor = deityColor,
            modifier = modifier,
            size = size,
            showLabel = showLabel,
            imageResName = imageResName,
            onClick = onClick,
        )
    }
}

/**
 * Content-only composable for deity images in custom containers
 * (e.g. VirtualPoojaRoomScreen chips, MandirAltarArea).
 *
 * This composable fills its parent container and renders:
 *   1. The user's custom photo if one has been set
 *   2. The bundled drawable if available
 *   3. A text initials fallback
 *
 * The caller is responsible for sizing, clipping, and border decorations.
 */
@Composable
fun SmartDeityImageBox(
    deityId: Int,
    nameTelugu: String,
    nameEnglish: String,
    deityColor: Color,
    imageResName: String? = null,
    modifier: Modifier = Modifier.fillMaxSize(),
    contentScale: ContentScale = ContentScale.Crop,
) {
    val prefs = koinInject<UserPreferencesManager>()
    var customPath by remember(deityId) { mutableStateOf<String?>(null) }
    LaunchedEffect(deityId) {
        customPath = prefs.getCustomDeityImagePath(deityId)
    }

    val drawableRes = remember(imageResName) { imageResName?.let { getDeityDrawable(it) } }

    when {
        customPath != null -> {
            AsyncImage(
                model = customPath,
                contentDescription = nameEnglish,
                modifier = modifier,
                contentScale = contentScale,
            )
        }
        drawableRes != null -> {
            androidx.compose.foundation.Image(
                painter = painterResource(drawableRes),
                contentDescription = nameEnglish,
                modifier = modifier,
                contentScale = contentScale,
            )
        }
        else -> {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                Text(
                    text = nameTelugu.take(2),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = deityColor,
                )
            }
        }
    }
}
