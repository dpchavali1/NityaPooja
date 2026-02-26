package com.nityapooja.shared.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.nityapooja.shared.platform.shareText
import com.nityapooja.shared.ui.theme.TempleGold

@Composable
fun ShareButton(
    contentBuilder: () -> String,
) {
    IconButton(onClick = {
        val text = contentBuilder()
        shareText(text, title = "Share")
    }) {
        Icon(Icons.Default.Share, contentDescription = "Share", tint = TempleGold)
    }
}

fun buildShareText(
    titleTelugu: String,
    titleEnglish: String,
    content: String,
    type: String = "",
): String = buildString {
    if (type.isNotBlank()) append("$type\n\n")
    append("$titleTelugu\n$titleEnglish\n\n")
    append(content)
    append("\n\nShared via NityaPooja")
}
