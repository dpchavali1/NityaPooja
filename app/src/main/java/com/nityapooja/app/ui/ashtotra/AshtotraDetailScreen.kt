package com.nityapooja.app.ui.ashtotra

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.nityapooja.app.ui.components.VerseBlock

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AshtotraDetailScreen(
    ashtotraId: Int,
    viewModel: AshtotraViewModel = hiltViewModel(),
    onBack: () -> Unit,
    audioViewModel: AudioPlayerViewModel,
    fontSizeViewModel: FontSizeViewModel = hiltViewModel(),
) {
    val ashtotraFlow = remember(ashtotraId) { viewModel.getAshtotraById(ashtotraId) }
    val ashtotra by ashtotraFlow.collectAsStateWithLifecycle()

    val fontSize by fontSizeViewModel.fontSize.collectAsStateWithLifecycle()
    val fontScale = fontSize / 16f
    val isSpotifyConnected by audioViewModel.isSpotifyConnected.collectAsStateWithLifecycle()
    val audioState by audioViewModel.state.collectAsStateWithLifecycle()
    val downloadProgress by audioViewModel.downloadProgress.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(ashtotra) {
        ashtotra?.let { viewModel.trackHistory("ashtotra", it.id, it.title, it.titleTelugu) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ashtotra?.let {
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
                    } ?: Text("అష్టోత్రం · Ashtottaram")
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
                    ashtotra?.let { a ->
                        IconButton(onClick = {
                            val text = buildShareText(
                                a.titleTelugu, a.title,
                                (a.namesTelugu ?: "") + "\n\n" + (a.names ?: ""),
                                "Ashtotra",
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
                    // ashtotra?.audioUrl?.let { url ->
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
                            ashtotra?.let { a ->
                                val query = SpotifySearchQueryBuilder.buildQuery(a.title, "ashtotharam")
                                audioViewModel.playViaSpotify(query, a.title, a.titleTelugu)
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
        ashtotra?.let { a ->
            val teluguNames = a.namesTelugu
                ?.split("\n")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList()

            val englishNames = a.names
                ?.split("\n")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList()

            val maxCount = maxOf(teluguNames.size, englishNames.size)

            if (maxCount == 0) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "నామాలు త్వరలో అందుబాటులో ఉంటాయి",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            "Sacred names coming soon",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // YouTube button
                    a.youtubeUrl?.let { url ->
                        item {
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
                    }

                    // Metadata header
                    item {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            MetadataChip(
                                label = "108 నామాలు · 108 Sacred Names",
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.primary,
                            )

                            if (a.duration > 0) {
                                val minutes = a.duration / 60
                                val seconds = a.duration % 60
                                MetadataChip(
                                    label = "${minutes}:${"%02d".format(seconds)}",
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(Modifier.height(8.dp))
                    }

                    // Names list using VerseBlock
                    itemsIndexed(
                        items = (0 until maxCount).toList(),
                        key = { index, _ -> index },
                    ) { index, _ ->
                        val teluguName = teluguNames.getOrNull(index)
                        val englishName = englishNames.getOrNull(index)

                        VerseBlock(
                            verseNumber = index + 1,
                            teluguText = teluguName,
                            englishText = englishName,
                            fontScale = fontScale,
                        )
                    }

                    // Bottom spacing for FAB
                    item {
                        Spacer(Modifier.height(72.dp))
                    }
                }
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
