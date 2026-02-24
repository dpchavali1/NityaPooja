package com.nityapooja.app.ui.suprabhatam

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WbSunny
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
fun SuprabhatamDetailScreen(
    suprabhatamId: Int,
    viewModel: SuprabhatamViewModel = hiltViewModel(),
    onBack: () -> Unit,
    audioViewModel: AudioPlayerViewModel,
    fontSizeViewModel: FontSizeViewModel = hiltViewModel(),
) {
    val suprabhatamFlow = remember(suprabhatamId) { viewModel.getSuprabhatamById(suprabhatamId) }
    val suprabhatam by suprabhatamFlow.collectAsStateWithLifecycle()

    val fontSize by fontSizeViewModel.fontSize.collectAsStateWithLifecycle()
    val fontScale = fontSize / 16f
    val isSpotifyConnected by audioViewModel.isSpotifyConnected.collectAsStateWithLifecycle()
    val audioState by audioViewModel.state.collectAsStateWithLifecycle()
    val downloadProgress by audioViewModel.downloadProgress.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(suprabhatam) {
        suprabhatam?.let { viewModel.trackHistory("suprabhatam", it.id, it.title, it.titleTelugu) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    suprabhatam?.let {
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
                    } ?: Text("సుప్రభాతం · Suprabhatam")
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
                    suprabhatam?.let { s ->
                        IconButton(onClick = {
                            val text = buildShareText(
                                s.titleTelugu, s.title,
                                (s.textTelugu ?: "") + "\n\n" + (s.textEnglish ?: ""),
                                "Suprabhatam",
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
                    // suprabhatam?.audioUrl?.let { url ->
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
                            suprabhatam?.let { s ->
                                val query = SpotifySearchQueryBuilder.buildQuery(s.title, "suprabhatam")
                                audioViewModel.playViaSpotify(query, s.title, s.titleTelugu)
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
        suprabhatam?.let { s ->
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
                    if (s.verseCount > 0) {
                        MetadataChip(
                            label = "${s.verseCount} శ్లోకాలు · ${s.verseCount} Verses",
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary,
                        )
                    }

                    if (s.duration > 0) {
                        val minutes = s.duration / 60
                        val seconds = s.duration % 60
                        MetadataChip(
                            label = "${minutes}:${"%02d".format(seconds)}",
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // YouTube button
                s.youtubeUrl?.let { url ->
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

                // Sanskrit Text
                s.textSanskrit?.let { text ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "సంస్కృతం · Sanskrit",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = (32 * fontScale).sp,
                                fontSize = (18 * fontScale).sp,
                            ),
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                // Telugu Text
                s.textTelugu?.let { text ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (s.textSanskrit != null) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(Modifier.height(4.dp))
                        }
                        Text(
                            "తెలుగు · Telugu",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = (32 * fontScale).sp,
                                fontSize = (18 * fontScale).sp,
                            ),
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                // English Text
                s.textEnglish?.let { text ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "English",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = (26 * fontScale).sp,
                                fontSize = (14 * fontScale).sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Empty state when no text
                if (s.textSanskrit == null && s.textTelugu == null && s.textEnglish == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.WbSunny,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "పాఠం త్వరలో అందుబాటులో ఉంటుంది",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                "Text coming soon",
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
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}
