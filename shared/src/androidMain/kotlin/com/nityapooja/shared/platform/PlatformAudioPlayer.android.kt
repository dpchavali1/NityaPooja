package com.nityapooja.shared.platform

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

actual class PlatformAudioPlayer(private val context: Context) {
    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    actual val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    private var player: ExoPlayer? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private fun getOrCreatePlayer(): ExoPlayer {
        return player ?: ExoPlayer.Builder(context).build().also { newPlayer ->
            player = newPlayer
            newPlayer.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _playbackState.update { it.copy(isPlaying = isPlaying) }
                }
                override fun onPlayerError(error: PlaybackException) {
                    _playbackState.update { it.copy(isBuffering = false, isPlaying = false) }
                }
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> _playbackState.update { it.copy(isBuffering = true) }
                        Player.STATE_READY -> _playbackState.update {
                            it.copy(isBuffering = false, duration = newPlayer.duration.coerceAtLeast(0))
                        }
                        Player.STATE_ENDED -> _playbackState.update {
                            it.copy(isPlaying = false, currentPosition = 0L)
                        }
                        else -> {}
                    }
                }
            })
            scope.launch {
                while (true) {
                    delay(500)
                    player?.let { p ->
                        if (p.isPlaying && p.duration > 0) {
                            _playbackState.update { it.copy(currentPosition = p.currentPosition) }
                        }
                    }
                }
            }
        }
    }

    actual fun play(url: String) {
        val p = getOrCreatePlayer()
        _playbackState.update { it.copy(isBuffering = true, currentPosition = 0L) }
        try {
            p.setMediaItem(MediaItem.fromUri(url))
            p.prepare()
            p.play()
        } catch (e: Exception) {
            _playbackState.update { it.copy(isBuffering = false) }
        }
    }

    actual fun pause() { player?.pause() }
    actual fun resume() { player?.play() }
    actual fun stop() {
        player?.stop()
        _playbackState.value = AudioPlaybackState()
    }
    actual fun seekTo(position: Long) { player?.seekTo(position) }
    actual fun release() {
        scope.cancel()
        player?.release()
        player = null
    }
}
