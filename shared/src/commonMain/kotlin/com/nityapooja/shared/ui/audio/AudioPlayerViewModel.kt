package com.nityapooja.shared.ui.audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.platform.PlatformAudioPlayer
import com.nityapooja.shared.platform.SpotifyPlaybackBridge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AudioSource { NONE, LOCAL, SPOTIFY }

data class AudioTrack(
    val url: String,
    val title: String,
    val titleTelugu: String,
)

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val currentTrack: AudioTrack? = null,
    val progress: Float = 0f,
    val duration: Long = 0L,
    val currentPosition: Long = 0L,
    val isBuffering: Boolean = false,
    val audioSource: AudioSource = AudioSource.NONE,
)

class AudioPlayerViewModel(
    private val audioPlayer: PlatformAudioPlayer,
    private val spotifyBridge: SpotifyPlaybackBridge,
) : ViewModel() {

    private val _currentTrack = MutableStateFlow<AudioTrack?>(null)
    private val _currentSource = MutableStateFlow(AudioSource.NONE)
    private val _isBuffering = MutableStateFlow(false)

    val isSpotifyConnected: StateFlow<Boolean> = spotifyBridge.isConnected

    val state: StateFlow<AudioPlayerState> = combine(
        audioPlayer.playbackState,
        spotifyBridge.playerState,
        _currentTrack,
        _currentSource,
        _isBuffering,
    ) { localState, spotifyState, track, source, buffering ->
        when (source) {
            AudioSource.SPOTIFY -> AudioPlayerState(
                isPlaying = spotifyState.isPlaying,
                currentTrack = track,
                progress = if (spotifyState.durationMs > 0) spotifyState.positionMs.toFloat() / spotifyState.durationMs.toFloat() else 0f,
                duration = spotifyState.durationMs,
                currentPosition = spotifyState.positionMs,
                isBuffering = buffering,
                audioSource = AudioSource.SPOTIFY,
            )
            AudioSource.LOCAL -> AudioPlayerState(
                isPlaying = localState.isPlaying,
                currentTrack = track,
                progress = if (localState.duration > 0) localState.currentPosition.toFloat() / localState.duration.toFloat() else 0f,
                duration = localState.duration,
                currentPosition = localState.currentPosition,
                isBuffering = localState.isBuffering,
                audioSource = AudioSource.LOCAL,
            )
            AudioSource.NONE -> AudioPlayerState(
                currentTrack = track,
                isBuffering = buffering,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AudioPlayerState())

    /** Play a local/streaming audio URL via PlatformAudioPlayer */
    fun playTrack(url: String, title: String, titleTelugu: String) {
        if (url.isBlank()) return
        val track = AudioTrack(url, title, titleTelugu)

        // If same track playing, toggle pause
        if (_currentTrack.value?.url == url && state.value.isPlaying) {
            audioPlayer.pause()
            return
        }

        // Stop Spotify if it was playing
        if (_currentSource.value == AudioSource.SPOTIFY) {
            spotifyBridge.stop()
        }

        _currentSource.value = AudioSource.LOCAL
        _currentTrack.value = track
        audioPlayer.play(url)
    }

    /** Search Spotify and play the best matching track */
    fun playViaSpotify(query: String, title: String, titleTelugu: String) {
        // Stop local player if it was playing
        if (_currentSource.value == AudioSource.LOCAL) {
            audioPlayer.stop()
        }

        _currentSource.value = AudioSource.SPOTIFY
        _currentTrack.value = AudioTrack(query, title, titleTelugu)
        _isBuffering.value = true

        viewModelScope.launch {
            val success = spotifyBridge.searchAndPlay(query, title, titleTelugu)
            _isBuffering.value = false
            if (!success) {
                _currentTrack.value = null
                _currentSource.value = AudioSource.NONE
            }
        }
    }

    fun pause() {
        when (_currentSource.value) {
            AudioSource.LOCAL -> audioPlayer.pause()
            AudioSource.SPOTIFY -> spotifyBridge.pause()
            AudioSource.NONE -> {}
        }
    }

    fun resume() {
        when (_currentSource.value) {
            AudioSource.LOCAL -> audioPlayer.resume()
            AudioSource.SPOTIFY -> spotifyBridge.resume()
            AudioSource.NONE -> {}
        }
    }

    fun togglePlayPause() {
        if (state.value.isPlaying) pause() else resume()
    }

    fun seekTo(position: Long) = audioPlayer.seekTo(position)

    fun stop() {
        when (_currentSource.value) {
            AudioSource.LOCAL -> audioPlayer.stop()
            AudioSource.SPOTIFY -> spotifyBridge.stop()
            AudioSource.NONE -> {}
        }
        _currentTrack.value = null
        _currentSource.value = AudioSource.NONE
        _isBuffering.value = false
    }

    override fun onCleared() {
        audioPlayer.release()
        super.onCleared()
    }
}
