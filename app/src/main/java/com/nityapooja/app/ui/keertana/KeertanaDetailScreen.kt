package com.nityapooja.app.ui.keertana

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.ui.audio.AudioPlayerViewModel
import androidx.compose.material.icons.filled.Share
import com.nityapooja.app.ui.components.DownloadButton
import com.nityapooja.app.ui.components.FontSizeControls
import com.nityapooja.app.ui.components.FontSizeViewModel
import com.nityapooja.app.ui.components.buildShareText
import com.nityapooja.app.ui.theme.TempleGold
import com.nityapooja.app.data.spotify.SpotifySearchQueryBuilder
import com.nityapooja.app.ui.theme.SpotifyGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun KeertanaDetailScreen(
    keertanaId: Int,
    viewModel: KeertanaViewModel = hiltViewModel(),
    onBack: () -> Unit,
    audioViewModel: AudioPlayerViewModel,
    fontSizeViewModel: FontSizeViewModel = hiltViewModel(),
) {
    val keertanaFlow = remember(keertanaId) { viewModel.getKeertanaById(keertanaId) }
    val keertana by keertanaFlow.collectAsStateWithLifecycle()

    val fontSize by fontSizeViewModel.fontSize.collectAsStateWithLifecycle()
    val fontScale = fontSize / 16f
    val isSpotifyConnected by audioViewModel.isSpotifyConnected.collectAsStateWithLifecycle()
    val audioState by audioViewModel.state.collectAsStateWithLifecycle()
    val downloadProgress by audioViewModel.downloadProgress.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(keertana) {
        keertana?.let { viewModel.trackHistory("keertana", it.id, it.title, it.titleTelugu) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    keertana?.let {
                        Column {
                            Text(
                                it.titleTelugu,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                            )
                            Text(
                                it.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                            )
                        }
                    } ?: Text("కీర్తన · Keertana")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    // Share button
                    keertana?.let { k ->
                        IconButton(onClick = {
                            val text = buildShareText(
                                k.titleTelugu, k.title,
                                (k.lyricsTelugu ?: "") + "\n\n" + (k.lyricsEnglish ?: ""),
                                "Keertana",
                            )
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, text)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share"))
                        }) {
                            Icon(Icons.Default.Share, "Share", tint = TempleGold)
                        }
                    }
                    // Download button (audio not yet available)
                    // keertana?.audioUrl?.let { url ->
                    //     DownloadButton(
                    //         audioUrl = url,
                    //         isDownloaded = audioViewModel.isDownloaded(url),
                    //         downloadProgress = downloadProgress,
                    //         onDownload = { audioViewModel.downloadTrack(it) },
                    //     )
                    // }
                    FontSizeControls(
                        fontSize = fontSize,
                        onDecrease = fontSizeViewModel::decrease,
                        onIncrease = fontSizeViewModel::increase,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        floatingActionButton = {
            val isCurrentTrackPlaying = audioState.isPlaying &&
                audioState.audioSource == com.nityapooja.app.ui.audio.AudioSource.SPOTIFY &&
                audioState.currentTrack != null

            FloatingActionButton(
                onClick = {
                    if (isSpotifyConnected) {
                        if (isCurrentTrackPlaying) {
                            audioViewModel.togglePlayPause()
                        } else {
                            keertana?.let { k ->
                                val query = SpotifySearchQueryBuilder.buildQuery(k.title, "keertana")
                                audioViewModel.playViaSpotify(query, k.title, k.titleTelugu)
                            }
                        }
                    }
                },
                containerColor = if (isSpotifyConnected) SpotifyGreen else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isSpotifyConnected) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            ) {
                Icon(
                    if (isCurrentTrackPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isCurrentTrackPlaying) "Pause" else "Play",
                )
            }
        },
    ) { padding ->
        keertana?.let { k ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Metadata Chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    k.composerTelugu?.let { composerTelugu ->
                        MetadataChip(
                            label = composerTelugu,
                            sublabel = k.composer,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary,
                        )
                    } ?: k.composer?.let { composer ->
                        MetadataChip(
                            label = composer,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary,
                        )
                    }

                    k.ragam?.let { ragam ->
                        MetadataChip(
                            label = "రాగం: $ragam",
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondary,
                        )
                    }

                    k.talam?.let { talam ->
                        MetadataChip(
                            label = "తాళం: $talam",
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.tertiary,
                        )
                    }

                    if (k.duration > 0) {
                        val minutes = k.duration / 60
                        val seconds = k.duration % 60
                        MetadataChip(
                            label = "${minutes}:${"%02d".format(seconds)}",
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // YouTube button
                k.youtubeUrl?.let { url ->
                    FilledTonalButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) {
                        Icon(Icons.Default.OndemandVideo, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Watch on YouTube")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Telugu Lyrics
                k.lyricsTelugu?.let { lyrics ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "సాహిత్యం · Lyrics (Telugu)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            lyrics,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = (32 * fontScale).sp,
                                fontSize = (18 * fontScale).sp,
                            ),
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                // English Lyrics
                k.lyricsEnglish?.let { lyrics ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Lyrics (English)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            lyrics,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = (26 * fontScale).sp,
                                fontSize = (14 * fontScale).sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Empty state when no lyrics
                if (k.lyricsTelugu == null && k.lyricsEnglish == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.MusicNote,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "సాహిత్యం త్వరలో అందుబాటులో ఉంటుంది",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                "Lyrics coming soon",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            )
                        }
                    }
                }

                // Bottom spacing for FAB
                Spacer(Modifier.height(72.dp))
            }
        } ?: run {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun MetadataChip(
    label: String,
    sublabel: String? = null,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = FontWeight.Medium,
            )
            sublabel?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.7f),
                )
            }
        }
    }
}
