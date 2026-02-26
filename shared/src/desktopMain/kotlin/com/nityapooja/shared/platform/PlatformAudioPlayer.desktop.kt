package com.nityapooja.shared.platform

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.BufferedInputStream
import java.net.URI
import javax.sound.sampled.*

actual class PlatformAudioPlayer {
    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    actual val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    private var clip: Clip? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var progressJob: Job? = null

    actual fun play(url: String) {
        stop()
        scope.launch {
            try {
                _playbackState.update { it.copy(isBuffering = true) }
                val audioStream = AudioSystem.getAudioInputStream(
                    BufferedInputStream(URI(url).toURL().openStream())
                )
                val baseFormat = audioStream.format
                val decodedFormat = AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.sampleRate, 16,
                    baseFormat.channels, baseFormat.channels * 2,
                    baseFormat.sampleRate, false
                )
                val decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream)

                val newClip = AudioSystem.getClip()
                newClip.open(decodedStream)
                clip = newClip
                newClip.start()
                _playbackState.update {
                    it.copy(
                        isPlaying = true, isBuffering = false,
                        duration = newClip.microsecondLength / 1000,
                    )
                }
                startProgressTracking()
            } catch (e: Exception) {
                println("Desktop audio error: ${e.message}")
                _playbackState.update { it.copy(isBuffering = false) }
            }
        }
    }

    actual fun pause() {
        clip?.stop()
        _playbackState.update { it.copy(isPlaying = false) }
    }

    actual fun resume() {
        clip?.start()
        _playbackState.update { it.copy(isPlaying = true) }
    }

    actual fun stop() {
        progressJob?.cancel()
        clip?.stop()
        clip?.close()
        clip = null
        _playbackState.value = AudioPlaybackState()
    }

    actual fun seekTo(position: Long) {
        clip?.microsecondPosition = position * 1000
    }

    actual fun release() {
        scope.cancel()
        clip?.stop()
        clip?.close()
        clip = null
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                delay(500)
                clip?.let { c ->
                    _playbackState.update {
                        it.copy(currentPosition = c.microsecondPosition / 1000)
                    }
                    if (!c.isRunning && c.microsecondPosition >= c.microsecondLength) {
                        _playbackState.update { it.copy(isPlaying = false) }
                        cancel()
                    }
                }
            }
        }
    }
}
