package com.nityapooja.shared.platform

import kotlinx.coroutines.flow.StateFlow

/**
 * Cross-platform bridge for Spotify playback.
 * Android provides a real implementation backed by SpotifyManager + SpotifyApi.
 * iOS/Desktop provide no-op stubs (Spotify SDK is Android-only).
 */
interface SpotifyPlaybackBridge {
    /** Whether Spotify is connected and ready for playback */
    val isConnected: StateFlow<Boolean>

    /** Current Spotify player state */
    val playerState: StateFlow<SpotifyBridgePlayerState>

    /**
     * Search Spotify for a track and play it.
     * @param query Spotify search query
     * @param title English title for display
     * @param titleTelugu Telugu title for display
     */
    suspend fun searchAndPlay(query: String, title: String, titleTelugu: String): Boolean

    /** Pause Spotify playback */
    fun pause()

    /** Resume Spotify playback */
    fun resume()

    /** Stop and clear Spotify playback */
    fun stop()
}

data class SpotifyBridgePlayerState(
    val isPlaying: Boolean = false,
    val trackName: String = "",
    val artistName: String = "",
    val isPaused: Boolean = true,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
)

/**
 * No-op implementation for platforms without Spotify SDK (iOS, Desktop).
 */
class NoOpSpotifyPlaybackBridge : SpotifyPlaybackBridge {
    override val isConnected: StateFlow<Boolean> =
        kotlinx.coroutines.flow.MutableStateFlow(false)
    override val playerState: StateFlow<SpotifyBridgePlayerState> =
        kotlinx.coroutines.flow.MutableStateFlow(SpotifyBridgePlayerState())
    override suspend fun searchAndPlay(query: String, title: String, titleTelugu: String) = false
    override fun pause() {}
    override fun resume() {}
    override fun stop() {}
}
