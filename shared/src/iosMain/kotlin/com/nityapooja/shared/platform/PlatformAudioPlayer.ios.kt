package com.nityapooja.shared.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.*
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
actual class PlatformAudioPlayer {
    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    actual val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    private var player: AVPlayer? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var progressJob: Job? = null

    init {
        // Configure audio session for playback
        try {
            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryPlayback, null)
            session.setActive(true, null)
        } catch (_: Exception) {
            // Simulator may not support audio session
        }
    }

    private fun cmTimeToSeconds(getter: () -> kotlinx.cinterop.CValue<platform.CoreMedia.CMTime>): Double {
        return getter().useContents {
            if (timescale > 0) value.toDouble() / timescale.toDouble() else 0.0
        }
    }

    actual fun play(url: String) {
        // Cancel previous progress tracking
        progressJob?.cancel()

        val nsUrl = NSURL.URLWithString(url) ?: run {
            _playbackState.update { it.copy(isBuffering = false) }
            return
        }

        // Stop previous player
        player?.pause()

        val playerItem = AVPlayerItem(uRL = nsUrl)
        player = AVPlayer(playerItem = playerItem)
        player?.play()
        _playbackState.update { it.copy(isPlaying = true, isBuffering = true) }

        // Progress tracking
        progressJob = scope.launch {
            // Wait briefly for playback to start
            delay(500)
            _playbackState.update { it.copy(isBuffering = false) }

            while (isActive) {
                delay(500)
                player?.let { p ->
                    val currentTime = cmTimeToSeconds { p.currentTime() }
                    val duration = p.currentItem?.let { item ->
                        cmTimeToSeconds { item.duration }
                    } ?: 0.0

                    // Check if duration is valid (not NaN/infinite)
                    val durationMs = if (duration.isNaN() || duration.isInfinite() || duration <= 0) 0L
                        else (duration * 1000).toLong()
                    val positionMs = if (currentTime.isNaN() || currentTime.isInfinite()) 0L
                        else (currentTime * 1000).toLong()

                    _playbackState.update {
                        it.copy(
                            currentPosition = positionMs,
                            duration = durationMs,
                        )
                    }

                    // Auto-stop when track ends
                    if (durationMs > 0 && positionMs >= durationMs) {
                        _playbackState.update { it.copy(isPlaying = false) }
                        cancel()
                    }
                }
            }
        }
    }

    actual fun pause() {
        player?.pause()
        _playbackState.update { it.copy(isPlaying = false) }
    }

    actual fun resume() {
        player?.play()
        _playbackState.update { it.copy(isPlaying = true) }
    }

    actual fun stop() {
        progressJob?.cancel()
        player?.pause()
        player = null
        _playbackState.value = AudioPlaybackState()
    }

    actual fun seekTo(position: Long) {
        val time = CMTimeMakeWithSeconds(position.toDouble() / 1000.0, 600)
        player?.seekToTime(time)
    }

    actual fun release() {
        progressJob?.cancel()
        scope.cancel()
        player?.pause()
        player = null
    }
}
