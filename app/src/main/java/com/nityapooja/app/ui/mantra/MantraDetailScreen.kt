package com.nityapooja.app.ui.mantra

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.ui.audio.AudioPlayerViewModel
import androidx.compose.material.icons.filled.Share
import com.nityapooja.app.ui.components.DownloadButton
import com.nityapooja.app.ui.components.FontSizeControls
import com.nityapooja.app.ui.components.FontSizeViewModel
import com.nityapooja.app.ui.components.GoldGradientButton
import com.nityapooja.app.ui.components.buildShareText
import com.nityapooja.app.ui.theme.TempleGold
import com.nityapooja.app.data.spotify.SpotifySearchQueryBuilder
import com.nityapooja.app.ui.theme.SpotifyGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MantraDetailScreen(
    mantraId: Int,
    onBack: () -> Unit,
    onStartChanting: ((Int, String) -> Unit)? = null,
    audioViewModel: AudioPlayerViewModel,
    viewModel: MantraViewModel = hiltViewModel(),
    fontSizeViewModel: FontSizeViewModel = hiltViewModel(),
) {
    val mantra by remember(mantraId) { viewModel.getMantraById(mantraId) }
        .collectAsStateWithLifecycle(initialValue = null)

    val fontSize by fontSizeViewModel.fontSize.collectAsStateWithLifecycle()
    val fontScale = fontSize / 16f
    val isSpotifyConnected by audioViewModel.isSpotifyConnected.collectAsStateWithLifecycle()
    val audioState by audioViewModel.state.collectAsStateWithLifecycle()
    val downloadProgress by audioViewModel.downloadProgress.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(mantra) {
        mantra?.let { viewModel.trackHistory("mantra", it.id, it.title, it.titleTelugu) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    mantra?.let {
                        Column {
                            Text(it.titleTelugu, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(it.title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Share button
                    mantra?.let { m ->
                        IconButton(onClick = {
                            val text = buildShareText(
                                m.titleTelugu, m.title,
                                (m.sanskrit ?: "") + "\n\n" + (m.meaningEnglish ?: ""),
                                "Mantra",
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
                    // mantra?.audioUrl?.let { url ->
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
                            mantra?.let { m ->
                                val query = SpotifySearchQueryBuilder.buildQuery(m.title, "mantra")
                                audioViewModel.playViaSpotify(query, m.title, m.titleTelugu)
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
        mantra?.let { m ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Sanskrit/Telugu text - large, centered
                m.sanskrit?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = (20 * fontScale).sp,
                            lineHeight = (40 * fontScale).sp,
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // YouTube button
                m.youtubeUrl?.let { url ->
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
                    Spacer(Modifier.height(8.dp))
                }

                // Recommended count
                AssistChip(
                    onClick = {},
                    label = { Text("Recommended: ${m.recommendedCount} times") },
                )

                Spacer(Modifier.height(16.dp))

                // Start Chanting button
                if (onStartChanting != null) {
                    GoldGradientButton(
                        text = "Start Chanting",
                        onClick = { onStartChanting(mantraId, "mantra") },
                        modifier = Modifier.fillMaxWidth(0.7f),
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Telugu Meaning
                m.meaningTelugu?.let {
                    Text(
                        "అర్థం (Telugu)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = (16 * fontScale).sp,
                            lineHeight = (28 * fontScale).sp,
                        ),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // English Meaning
                m.meaningEnglish?.let {
                    Text(
                        "Meaning (English)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (14 * fontScale).sp,
                            lineHeight = (22 * fontScale).sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // Benefits
                m.benefits?.let {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    Text(
                        "Benefits",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (14 * fontScale).sp,
                            lineHeight = (22 * fontScale).sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } ?: Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
