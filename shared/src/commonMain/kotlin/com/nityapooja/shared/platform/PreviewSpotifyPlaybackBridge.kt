package com.nityapooja.shared.platform

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.nityapooja.shared.data.spotify.SpotifyApi

/**
 * SpotifyPlaybackBridge for platforms without Spotify SDK (iOS, Desktop).
 * Uses Spotify Web API (Client Credentials) to search tracks,
 * then opens the track in Spotify app or web player.
 *
 * Preview URLs have been deprecated by Spotify, so this bridge
 * opens the track externally instead of playing inline.
 */
class PreviewSpotifyPlaybackBridge(
    private val spotifyApi: SpotifyApi,
    private val audioPlayer: PlatformAudioPlayer,
) : SpotifyPlaybackBridge {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Default to true — Client Credentials needs no user setup.
    private val _isConnected = MutableStateFlow(true)
    override val isConnected: StateFlow<Boolean> = _isConnected

    private val _playerState = MutableStateFlow(SpotifyBridgePlayerState())
    override val playerState: StateFlow<SpotifyBridgePlayerState> = _playerState

    override suspend fun searchAndPlay(query: String, title: String, titleTelugu: String): Boolean {
        return try {
            val response = spotifyApi.searchTracksWithClientCredentials(query = query)
            if (response == null) {
                _isConnected.value = false
                return false
            }

            val items = response.tracks?.items ?: return false
            if (items.isEmpty()) return false

            // Try preview URL first (unlikely to exist, but try)
            val trackWithPreview = items.firstOrNull { it.previewUrl != null }
            if (trackWithPreview?.previewUrl != null) {
                _playerState.value = SpotifyBridgePlayerState(
                    isPlaying = true,
                    trackName = trackWithPreview.name,
                    artistName = trackWithPreview.artists.firstOrNull()?.name ?: "",
                )
                audioPlayer.play(trackWithPreview.previewUrl!!)
                return true
            }

            // No preview URL — open in Spotify app/web player
            val bestMatch = items.first()
            val spotifyWebUrl = "https://open.spotify.com/track/${bestMatch.id}"
            openUrl(spotifyWebUrl)

            // Update state to show what was opened
            _playerState.value = SpotifyBridgePlayerState(
                isPlaying = true,
                trackName = bestMatch.name,
                artistName = bestMatch.artists.firstOrNull()?.name ?: "",
            )

            true
        } catch (_: Exception) {
            false
        }
    }

    override fun pause() {
        audioPlayer.pause()
        _playerState.value = _playerState.value.copy(isPlaying = false, isPaused = true)
    }

    override fun resume() {
        audioPlayer.resume()
        _playerState.value = _playerState.value.copy(isPlaying = true, isPaused = false)
    }

    override fun stop() {
        audioPlayer.stop()
        _playerState.value = SpotifyBridgePlayerState()
    }
}
