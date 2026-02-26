package com.nityapooja.shared.platform

import kotlinx.coroutines.flow.StateFlow

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isBuffering: Boolean = false,
)

expect class PlatformAudioPlayer {
    val playbackState: StateFlow<AudioPlaybackState>

    fun play(url: String)
    fun pause()
    fun resume()
    fun stop()
    fun seekTo(position: Long)
    fun release()
}
