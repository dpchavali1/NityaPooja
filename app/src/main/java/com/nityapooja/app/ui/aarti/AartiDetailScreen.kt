package com.nityapooja.app.ui.aarti

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.data.spotify.SpotifySearchQueryBuilder
import com.nityapooja.app.ui.audio.AudioPlayerViewModel
import com.nityapooja.app.ui.components.FontSizeControls
import com.nityapooja.app.ui.components.FontSizeViewModel
import com.nityapooja.app.ui.components.buildShareText
import com.nityapooja.app.ui.theme.SpotifyGreen
import com.nityapooja.app.ui.theme.TempleGold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AartiDetailScreen(
    aartiId: Int,
    onBack: () -> Unit,
    audioViewModel: AudioPlayerViewModel,
    viewModel: AartiViewModel = hiltViewModel(),
    fontSizeViewModel: FontSizeViewModel = hiltViewModel(),
) {
    val aarti by viewModel.getAartiById(aartiId)
        .collectAsStateWithLifecycle(initialValue = null)

    val fontSize by fontSizeViewModel.fontSize.collectAsStateWithLifecycle()
    val fontScale = fontSize / 16f
    val isSpotifyConnected by audioViewModel.isSpotifyConnected.collectAsStateWithLifecycle()
    val audioState by audioViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Track reading history
    LaunchedEffect(aarti) {
        aarti?.let { viewModel.trackHistory("aarti", it.id, it.title, it.titleTelugu) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            aarti?.titleTelugu ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            aarti?.title ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Share button
                    aarti?.let { a ->
                        IconButton(onClick = {
                            val text = buildShareText(
                                a.titleTelugu, a.title,
                                (a.lyricsTelugu ?: "") + "\n\n" + (a.lyricsEnglish ?: ""),
                                "Aarti",
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
                    // Download button (commented out - audio not yet available)
                    // aarti?.audioUrl?.let { url ->
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
                            aarti?.let { a ->
                                val query = SpotifySearchQueryBuilder.buildQuery(a.title, "aarti")
                                audioViewModel.playViaSpotify(query, a.title, a.titleTelugu)
                            }
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Spotify not connected. Go to Settings to connect.",
                                actionLabel = "OK",
                                duration = SnackbarDuration.Short,
                            )
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
        aarti?.let { currentAarti ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                // YouTube button
                currentAarti.youtubeUrl?.let { url ->
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
                    Spacer(Modifier.height(16.dp))
                }

                // Telugu lyrics
                currentAarti.lyricsTelugu?.let { lyrics ->
                    Text(
                        "తెలుగు సాహిత్యం",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        lyrics,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = (16 * fontScale).sp,
                            lineHeight = (16 * fontScale * 1.4).sp,
                        ),
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(24.dp))
                }

                // English lyrics
                currentAarti.lyricsEnglish?.let { lyrics ->
                    Text(
                        "English Lyrics",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        lyrics,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (14 * fontScale).sp,
                            lineHeight = (14 * fontScale * 1.5).sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(24.dp))
                }

                // Duration info (commented out - audio not yet available)
                // if (currentAarti.duration > 0) {
                //     val minutes = currentAarti.duration / 60
                //     val seconds = currentAarti.duration % 60
                //     Text(
                //         "వ్యవధి · Duration: ${minutes}m ${seconds}s",
                //         style = MaterialTheme.typography.labelSmall,
                //         color = MaterialTheme.colorScheme.onSurfaceVariant,
                //     )
                //     Spacer(Modifier.height(16.dp))
                // }

                // Audio source attribution (commented out - audio not yet available)
                // currentAarti.audioSource?.let { source ->
                //     Text(
                //         "ఆడియో మూలం · Source: $source",
                //         style = MaterialTheme.typography.labelSmall,
                //         color = MaterialTheme.colorScheme.onSurfaceVariant,
                //     )
                // }

                Spacer(Modifier.height(24.dp))
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
