package com.nityapooja.shared.ui.chalisa

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
import com.nityapooja.shared.ui.components.GoldGradientButton
import com.nityapooja.shared.ui.components.buildShareText
import com.nityapooja.shared.ui.theme.TempleGold
import com.nityapooja.shared.platform.shareText
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import com.nityapooja.shared.data.spotify.SpotifySearchQueryBuilder
import com.nityapooja.shared.ui.audio.AudioSource
import com.nityapooja.shared.ui.theme.SpotifyGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChalisaDetailScreen(
    chalisaId: Int,
    onBack: () -> Unit,
    onStartChanting: ((Int, String) -> Unit)? = null,
    audioViewModel: AudioPlayerViewModel,
    viewModel: ChalisaViewModel = koinViewModel(),
    fontSizeViewModel: FontSizeViewModel = koinViewModel(),
    bannerAd: (@Composable () -> Unit)? = null,
) {
    val chalisa by remember(chalisaId) {
        viewModel.getChalisaById(chalisaId)
    }.collectAsState(initial = null)

    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f
    val audioState by audioViewModel.state.collectAsState()
    val isSpotifyConnected by audioViewModel.isSpotifyConnected.collectAsState()
    val isSpotifyLinked by audioViewModel.isSpotifyLinked.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(chalisa) {
        chalisa?.let { viewModel.trackHistory("chalisa", it.id, it.title, it.titleTelugu) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            chalisa?.titleTelugu ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                        Text(
                            chalisa?.title ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
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
                    chalisa?.let { c ->
                        IconButton(onClick = {
                            val text = buildShareText(
                                c.titleTelugu, c.title,
                                (c.chaupaiTelugu ?: "") + "\n\n" + (c.chaupai ?: ""),
                                "Chalisa",
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
            chalisa?.let { c ->
                val isCurrentTrackPlaying = audioState.isPlaying &&
                    audioState.audioSource == AudioSource.SPOTIFY &&
                    audioState.currentTrack != null

                FloatingActionButton(
                    onClick = {
                        if (isSpotifyConnected || isSpotifyLinked) {
                            if (isCurrentTrackPlaying) {
                                audioViewModel.togglePlayPause()
                            } else {
                                val query = SpotifySearchQueryBuilder.buildQuery(c.title, "chalisa")
                                audioViewModel.playViaSpotify(query, c.title, c.titleTelugu)
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
        val currentChalisa = chalisa
        if (currentChalisa == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                // Metadata row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (currentChalisa.verseCount > 0) {
                        AssistChip(
                            onClick = {},
                            label = { Text("${currentChalisa.verseCount} verses") },
                            shape = RoundedCornerShape(8.dp),
                        )
                    }
                    if (currentChalisa.duration > 0) {
                        AssistChip(
                            onClick = {},
                            label = { Text(formatDetailDuration(currentChalisa.duration)) },
                            shape = RoundedCornerShape(8.dp),
                        )
                    }
                }

                // Start Chanting button
                if (onStartChanting != null) {
                    Spacer(Modifier.height(12.dp))
                    GoldGradientButton(
                        text = "Start Chanting",
                        onClick = { onStartChanting(chalisaId, "chalisa") },
                        modifier = Modifier.fillMaxWidth(0.7f),
                    )
                }

                bannerAd?.invoke()

                // Doha (introductory verse)
                val dohaTelugu = currentChalisa.dohaTelugu
                val doha = currentChalisa.doha
                if (!dohaTelugu.isNullOrBlank() || !doha.isNullOrBlank()) {
                    Spacer(Modifier.height(20.dp))

                    Text(
                        "దోహా · Doha",
                        style = MaterialTheme.typography.labelMedium,
                        color = TempleGold,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        ),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (!dohaTelugu.isNullOrBlank()) {
                                Text(
                                    dohaTelugu.trim(),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = (20 * fontScale).sp,
                                        lineHeight = (32 * fontScale).sp,
                                    ),
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                            if (!doha.isNullOrBlank()) {
                                Text(
                                    doha.trim(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = (14 * fontScale).sp,
                                        lineHeight = (22 * fontScale).sp,
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                // Chaupai (main verses)
                val chaupaiTelugu = currentChalisa.chaupaiTelugu
                val chaupaiEnglish = currentChalisa.chaupai
                if (!chaupaiTelugu.isNullOrBlank() || !chaupaiEnglish.isNullOrBlank()) {
                    Spacer(Modifier.height(24.dp))

                    Text(
                        "చౌపాయీ · Chaupai",
                        style = MaterialTheme.typography.labelMedium,
                        color = TempleGold,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(12.dp))

                    val teluguVerses = chaupaiTelugu
                        ?.split("\n\n")
                        ?.filter { it.isNotBlank() }
                        ?: emptyList()
                    val englishVerses = chaupaiEnglish
                        ?.split("\n\n")
                        ?.filter { it.isNotBlank() }
                        ?: emptyList()

                    val verseCount = maxOf(teluguVerses.size, englishVerses.size)
                    for (i in 0 until verseCount) {
                        if (i > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant,
                            )
                        }

                        // Verse number
                        Text(
                            "Verse ${i + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(8.dp))

                        // Telugu text (large, primary color)
                        if (i < teluguVerses.size) {
                            Text(
                                teluguVerses[i].trim(),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = (20 * fontScale).sp,
                                    lineHeight = (32 * fontScale).sp,
                                ),
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // English text (smaller, gray)
                        if (i < englishVerses.size) {
                            Text(
                                englishVerses[i].trim(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = (14 * fontScale).sp,
                                    lineHeight = (22 * fontScale).sp,
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // Bottom spacing for FAB
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

private fun formatDetailDuration(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return if (minutes > 0) "${minutes}m ${secs}s" else "${secs}s"
}
