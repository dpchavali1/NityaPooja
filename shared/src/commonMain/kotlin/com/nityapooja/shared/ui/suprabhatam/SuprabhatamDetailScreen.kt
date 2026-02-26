package com.nityapooja.shared.ui.suprabhatam

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import com.nityapooja.shared.data.spotify.SpotifySearchQueryBuilder
import com.nityapooja.shared.ui.audio.AudioPlayerViewModel
import com.nityapooja.shared.ui.audio.AudioSource
import com.nityapooja.shared.ui.components.FontSizeControls
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.components.buildShareText
import com.nityapooja.shared.ui.theme.SpotifyGreen
import com.nityapooja.shared.ui.theme.TempleGold
import com.nityapooja.shared.platform.shareText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SuprabhatamDetailScreen(
    suprabhatamId: Int,
    viewModel: SuprabhatamViewModel = koinViewModel(),
    onBack: () -> Unit,
    audioViewModel: AudioPlayerViewModel,
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
) {
    val suprabhatamFlow = remember(suprabhatamId) { viewModel.getSuprabhatamById(suprabhatamId) }
    val suprabhatam by suprabhatamFlow.collectAsState(initial = null)

    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f
    val audioState by audioViewModel.state.collectAsState()
    val isSpotifyConnected by audioViewModel.isSpotifyConnected.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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
            suprabhatam?.let { s ->
                val isCurrentTrackPlaying = audioState.isPlaying &&
                    audioState.audioSource == AudioSource.SPOTIFY &&
                    audioState.currentTrack != null

                FloatingActionButton(
                    onClick = {
                        if (isSpotifyConnected) {
                            if (isCurrentTrackPlaying) {
                                audioViewModel.togglePlayPause()
                            } else {
                                val query = SpotifySearchQueryBuilder.buildQuery(s.title, "suprabhatam")
                                audioViewModel.playViaSpotify(query, s.title, s.titleTelugu)
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
                            label = "${minutes}:${seconds.toString().padStart(2, '0')}",
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
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
