package com.nityapooja.shared.ui.aarti

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AartiDetailScreen(
    aartiId: Int,
    onBack: () -> Unit,
    audioViewModel: AudioPlayerViewModel,
    viewModel: AartiViewModel = koinViewModel(),
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val aarti by viewModel.getAartiById(aartiId)
        .collectAsState(initial = null)

    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f
    val audioState by audioViewModel.state.collectAsState()
    val isSpotifyConnected by audioViewModel.isSpotifyConnected.collectAsState()
    val isSpotifyLinked by audioViewModel.isSpotifyLinked.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Track reading history
    LaunchedEffect(aarti) {
        aarti?.let { viewModel.trackHistory("aarti", it.id, it.title, it.titleTelugu) }
    }

    Scaffold(
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
                    aarti?.let { a ->
                        IconButton(onClick = {
                            val text = buildShareText(
                                a.titleTelugu, a.title,
                                (a.lyricsTelugu ?: "") + "\n\n" + (a.lyricsEnglish ?: ""),
                                "Aarti",
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
            aarti?.let { a ->
                val isCurrentTrackPlaying = audioState.isPlaying &&
                    audioState.audioSource == AudioSource.SPOTIFY &&
                    audioState.currentTrack != null

                FloatingActionButton(
                    onClick = {
                        if (isSpotifyConnected || isSpotifyLinked) {
                            if (isCurrentTrackPlaying) {
                                audioViewModel.togglePlayPause()
                            } else {
                                val query = SpotifySearchQueryBuilder.buildQuery(a.title, "aarti")
                                audioViewModel.playViaSpotify(query, a.title, a.titleTelugu)
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
                    containerColor = if (isSpotifyConnected || isSpotifyLinked) SpotifyGreen else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isSpotifyConnected || isSpotifyLinked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                ) {
                    Icon(
                        if (isCurrentTrackPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isCurrentTrackPlaying) "Pause" else "Play",
                    )
                }
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
                bannerAd?.invoke()

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
