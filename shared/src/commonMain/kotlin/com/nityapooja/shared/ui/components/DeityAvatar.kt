package com.nityapooja.shared.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nityapooja.shared.generated.resources.Res
import nityapooja.shared.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

fun getDeityDrawable(resName: String): DrawableResource? = when (resName) {
    "deity_venkateswara" -> Res.drawable.deity_venkateswara
    "deity_ganesh" -> Res.drawable.deity_ganesh
    "deity_siva" -> Res.drawable.deity_siva
    "deity_hanuman" -> Res.drawable.deity_hanuman
    "deity_rama" -> Res.drawable.deity_rama
    "deity_lakshmi" -> Res.drawable.deity_lakshmi
    "deity_durga" -> Res.drawable.deity_durga
    "deity_krishna" -> Res.drawable.deity_krishna
    "deity_saraswati" -> Res.drawable.deity_saraswati
    "deity_vishnu" -> Res.drawable.deity_vishnu
    "deity_surya" -> Res.drawable.deity_surya
    "deity_subramanya" -> Res.drawable.deity_subramanya
    "deity_ayyappa" -> Res.drawable.deity_ayyappa
    "deity_sani" -> Res.drawable.deity_sani
    else -> null
}

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
    val drawableRes = imageResName?.let { getDeityDrawable(it) }

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
            if (drawableRes != null) {
                Image(
                    painter = painterResource(drawableRes),
                    contentDescription = nameEnglish,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
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
