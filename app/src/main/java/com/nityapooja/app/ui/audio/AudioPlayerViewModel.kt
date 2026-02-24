package com.nityapooja.app.ui.audio

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.nityapooja.app.audio.AudioDownloadManager
import com.nityapooja.app.data.spotify.SpotifyConnectionStatus
import com.nityapooja.app.data.spotify.SpotifyManager
import com.nityapooja.app.data.spotify.SpotifyWebApi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AudioTrack(
    val url: String,
    val title: String,
    val titleTelugu: String,
)

enum class AudioSource {
    NONE,
    EXOPLAYER,
    SPOTIFY,
}

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val currentTrack: AudioTrack? = null,
    val progress: Float = 0f,
    val duration: Long = 0L,
    val currentPosition: Long = 0L,
    val isBuffering: Boolean = false,
    val audioSource: AudioSource = AudioSource.NONE,
    val spotifySearchError: String? = null,
)

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadManager: AudioDownloadManager,
    private val spotifyManager: SpotifyManager,
    private val spotifyWebApi: SpotifyWebApi,
) : ViewModel() {

    private val _state = MutableStateFlow(AudioPlayerState())
    val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

    val downloadProgress = downloadManager.downloadProgress

    val isSpotifyConnected: StateFlow<Boolean> = spotifyManager.connectionStatus
        .map { it == SpotifyConnectionStatus.CONNECTED }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val spotifyConnectionStatus = spotifyManager.connectionStatus

    private var player: ExoPlayer? = null
    private var currentSource: AudioSource = AudioSource.NONE

    init {
        // Observe Spotify player state for progress updates
        viewModelScope.launch {
            spotifyManager.playerState.collect { spotifyState ->
                if (currentSource == AudioSource.SPOTIFY) {
                    _state.update {
                        it.copy(
                            isPlaying = !spotifyState.isPaused,
                            duration = spotifyState.durationMs,
                            currentPosition = spotifyState.positionMs,
                            progress = if (spotifyState.durationMs > 0) {
                                spotifyState.positionMs.toFloat() / spotifyState.durationMs.toFloat()
                            } else 0f,
                        )
                    }
                }
            }
        }
    }

    private fun getOrCreatePlayer(): ExoPlayer {
        return player ?: ExoPlayer.Builder(context).build().also { newPlayer ->
            player = newPlayer
            newPlayer.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (currentSource == AudioSource.EXOPLAYER) {
                        _state.update { it.copy(isPlaying = isPlaying) }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    _state.update { it.copy(isBuffering = false, isPlaying = false) }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (currentSource != AudioSource.EXOPLAYER) return
                    when (playbackState) {
                        Player.STATE_BUFFERING -> _state.update { it.copy(isBuffering = true) }
                        Player.STATE_READY -> _state.update {
                            it.copy(
                                isBuffering = false,
                                duration = newPlayer.duration.coerceAtLeast(0),
                            )
                        }
                        Player.STATE_ENDED -> {
                            _state.update { it.copy(isPlaying = false, progress = 0f, currentPosition = 0) }
                        }
                        else -> {}
                    }
                }
            })
            // Progress tracking
            viewModelScope.launch {
                while (true) {
                    delay(500)
                    if (currentSource == AudioSource.EXOPLAYER) {
                        player?.let { p ->
                            if (p.isPlaying && p.duration > 0) {
                                _state.update {
                                    it.copy(
                                        currentPosition = p.currentPosition,
                                        progress = p.currentPosition.toFloat() / p.duration.toFloat(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun playTrack(url: String, title: String, titleTelugu: String) {
        if (url.isBlank()) return

        // Stop Spotify if switching to ExoPlayer
        if (currentSource == AudioSource.SPOTIFY) {
            spotifyManager.pause()
        }

        currentSource = AudioSource.EXOPLAYER
        val track = AudioTrack(url, title, titleTelugu)
        val p = getOrCreatePlayer()

        // If same track, just toggle play/pause
        if (_state.value.currentTrack?.url == url && _state.value.audioSource == AudioSource.EXOPLAYER) {
            if (p.isPlaying) p.pause() else p.play()
            return
        }

        _state.update { it.copy(currentTrack = track, isBuffering = true, progress = 0f, audioSource = AudioSource.EXOPLAYER) }

        // Check for local file first
        val localPath = downloadManager.getLocalPath(url)
        val playUri = localPath ?: url

        try {
            p.setMediaItem(MediaItem.fromUri(playUri))
            p.prepare()
            p.play()
        } catch (e: Exception) {
            _state.update { it.copy(isBuffering = false, currentTrack = null) }
        }
    }

    fun playViaSpotify(searchQuery: String, title: String, titleTelugu: String) {
        // Stop ExoPlayer if switching to Spotify
        if (currentSource == AudioSource.EXOPLAYER) {
            player?.stop()
        }

        currentSource = AudioSource.SPOTIFY
        val track = AudioTrack(url = "spotify:search:$searchQuery", title = title, titleTelugu = titleTelugu)
        _state.update {
            it.copy(
                currentTrack = track,
                isBuffering = true,
                progress = 0f,
                audioSource = AudioSource.SPOTIFY,
                spotifySearchError = null,
            )
        }

        viewModelScope.launch {
            try {
                val tokenValid = spotifyManager.ensureTokenValid()
                if (!tokenValid) {
                    _state.update {
                        it.copy(isBuffering = false, spotifySearchError = "Spotify token expired. Please re-link in Settings.")
                    }
                    return@launch
                }

                val token = spotifyManager.getAccessToken() ?: return@launch
                val response = spotifyWebApi.searchTracks(
                    authorization = "Bearer $token",
                    query = searchQuery,
                )

                val bestMatch = response.tracks?.items?.firstOrNull()
                if (bestMatch != null) {
                    _state.update {
                        it.copy(
                            currentTrack = AudioTrack(
                                url = bestMatch.uri,
                                title = bestMatch.name,
                                titleTelugu = titleTelugu,
                            ),
                            isBuffering = false,
                        )
                    }
                    spotifyManager.connectAppRemote()
                    // Wait for connection before playing
                    spotifyManager.connectionStatus
                        .filter { it == SpotifyConnectionStatus.CONNECTED }
                        .first()
                    spotifyManager.play(bestMatch.uri)
                } else {
                    _state.update {
                        it.copy(
                            isBuffering = false,
                            spotifySearchError = "No matching track found on Spotify",
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isBuffering = false,
                        spotifySearchError = "Spotify search failed: ${e.message}",
                    )
                }
            }
        }
    }

    fun pause() {
        when (currentSource) {
            AudioSource.EXOPLAYER -> player?.pause()
            AudioSource.SPOTIFY -> spotifyManager.pause()
            AudioSource.NONE -> {}
        }
    }

    fun resume() {
        when (currentSource) {
            AudioSource.EXOPLAYER -> player?.play()
            AudioSource.SPOTIFY -> spotifyManager.resume()
            AudioSource.NONE -> {}
        }
    }

    fun togglePlayPause() {
        when (currentSource) {
            AudioSource.EXOPLAYER -> {
                player?.let { p ->
                    if (p.isPlaying) p.pause() else p.play()
                }
            }
            AudioSource.SPOTIFY -> {
                if (_state.value.isPlaying) {
                    spotifyManager.pause()
                } else {
                    spotifyManager.resume()
                }
            }
            AudioSource.NONE -> {}
        }
    }

    fun seekTo(position: Long) {
        if (currentSource == AudioSource.EXOPLAYER) {
            player?.seekTo(position)
        }
    }

    fun stop() {
        when (currentSource) {
            AudioSource.EXOPLAYER -> player?.stop()
            AudioSource.SPOTIFY -> spotifyManager.pause()
            AudioSource.NONE -> {}
        }
        currentSource = AudioSource.NONE
        _state.update { AudioPlayerState() }
    }

    fun isDownloaded(url: String): Boolean = downloadManager.isDownloaded(url)

    fun downloadTrack(url: String) {
        viewModelScope.launch {
            downloadManager.download(url)
        }
    }

    fun deleteDownload(url: String) {
        downloadManager.deleteDownload(url)
    }

    fun clearSpotifyError() {
        _state.update { it.copy(spotifySearchError = null) }
    }

    override fun onCleared() {
        player?.release()
        player = null
        super.onCleared()
    }
}
