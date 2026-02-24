package com.nityapooja.app.ui.audio

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nityapooja.app.ui.theme.SpotifyGreen
import com.nityapooja.app.ui.theme.TempleGold

@Composable
fun AudioMiniPlayer(
    viewModel: AudioPlayerViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isSpotify = state.audioSource == AudioSource.SPOTIFY
    val accentColor = if (isSpotify) SpotifyGreen else TempleGold

    AnimatedVisibility(
        visible = state.currentTrack != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier,
    ) {
        state.currentTrack?.let { track ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                shadowElevation = 8.dp,
                tonalElevation = 2.dp,
            ) {
                Column {
                    // Progress bar at top
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = accentColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Spotify badge
                        if (isSpotify) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SpotifyGreen,
                                modifier = Modifier.padding(end = 8.dp),
                            ) {
                                Text(
                                    "S",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = androidx.compose.ui.graphics.Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                )
                            }
                        }

                        // Track info
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.titleTelugu,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = track.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        // Play/Pause
                        if (state.isBuffering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = accentColor,
                            )
                        } else {
                            IconButton(onClick = { viewModel.togglePlayPause() }) {
                                Icon(
                                    imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (state.isPlaying) "Pause" else "Play",
                                    tint = accentColor,
                                )
                            }
                        }

                        // Close
                        IconButton(onClick = { viewModel.stop() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
