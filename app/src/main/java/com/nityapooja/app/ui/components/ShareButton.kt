package com.nityapooja.app.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.nityapooja.app.ui.theme.TempleGold

@Composable
fun ShareButton(
    contentBuilder: () -> String,
    context: Context,
) {
    IconButton(onClick = {
        val shareText = contentBuilder()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "Share"))
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
