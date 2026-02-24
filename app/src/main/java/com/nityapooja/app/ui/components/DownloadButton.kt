package com.nityapooja.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nityapooja.app.audio.DownloadProgress
import com.nityapooja.app.ui.theme.AuspiciousGreen
import com.nityapooja.app.ui.theme.TempleGold

@Composable
fun DownloadButton(
    audioUrl: String,
    isDownloaded: Boolean,
    downloadProgress: DownloadProgress,
    onDownload: (String) -> Unit,
) {
    if (audioUrl.isBlank()) return

    val isThisDownloading = downloadProgress.isDownloading && downloadProgress.url == audioUrl

    IconButton(onClick = {
        if (!isDownloaded && !isThisDownloading) {
            onDownload(audioUrl)
        }
    }) {
        AnimatedContent(
            targetState = when {
                isDownloaded -> "downloaded"
                isThisDownloading -> "downloading"
                else -> "not_downloaded"
            },
            label = "downloadState",
        ) { state ->
            when (state) {
                "downloaded" -> Icon(
                    Icons.Default.CloudDone,
                    contentDescription = "Downloaded",
                    tint = AuspiciousGreen,
                )
                "downloading" -> CircularProgressIndicator(
                    progress = { downloadProgress.progress },
                    modifier = Modifier.size(24.dp),
                    color = TempleGold,
                    strokeWidth = 2.dp,
                )
                else -> Icon(
                    Icons.Default.CloudDownload,
                    contentDescription = "Download for offline",
                    tint = TempleGold,
                )
            }
        }
    }
}
