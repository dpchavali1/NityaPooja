package com.nityapooja.shared.ui.keertana

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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.ui.audio.AudioPlayerViewModel
import com.nityapooja.shared.ui.components.FontSizeControls
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.components.buildShareText
import com.nityapooja.shared.ui.theme.TempleGold
import com.nityapooja.shared.platform.shareText
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import com.nityapooja.shared.data.spotify.SpotifySearchQueryBuilder
import com.nityapooja.shared.ui.audio.AudioSource
import com.nityapooja.shared.ui.theme.SpotifyGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun KeertanaDetailScreen(
    keertanaId: Int,
    viewModel: KeertanaViewModel = koinViewModel(),
    onBack: () -> Unit,
    audioViewModel: AudioPlayerViewModel,
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
) {
    val keertanaFlow = remember(keertanaId) { viewModel.getKeertanaById(keertanaId) }
    val keertana by keertanaFlow.collectAsState(initial = null)

    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f
    val audioState by audioViewModel.state.collectAsState()
    val isSpotifyConnected by audioViewModel.isSpotifyConnected.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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
                            shareText(text)
                        }) {
                            Icon(Icons.Default.Share, "Share", tint = TempleGold)
                        }
                    }
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            keertana?.let { k ->
                val isCurrentTrackPlaying = audioState.isPlaying &&
                    audioState.audioSource == AudioSource.SPOTIFY &&
                    audioState.currentTrack != null

                FloatingActionButton(
                    onClick = {
                        if (isSpotifyConnected) {
                            if (isCurrentTrackPlaying) {
                                audioViewModel.togglePlayPause()
                            } else {
                                val query = SpotifySearchQueryBuilder.buildQuery(k.title, "keertana")
                                audioViewModel.playViaSpotify(query, k.title, k.titleTelugu)
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Connect Spotify in Settings to play music",
                                    actionLabel = "OK",
                                    duration = SnackbarDuration.Short,
                                )
                            }
                        }
                    },
                    containerColor = if (isSpotifyConnected) SpotifyGreen else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isSpotifyConnected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                ) {
                    Icon(
                        if (isCurrentTrackPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isCurrentTrackPlaying) "Pause" else "Play",
                    )
                }
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
                            label = "${minutes}:${seconds.toString().padStart(2, '0')}",
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
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
