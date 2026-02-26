package com.nityapooja.app.data.spotify

import android.util.Log
import com.nityapooja.shared.data.spotify.SpotifyApi
import com.nityapooja.shared.platform.SpotifyBridgePlayerState
import com.nityapooja.shared.platform.SpotifyPlaybackBridge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.nityapooja.shared.data.preferences.UserPreferencesManager

class AndroidSpotifyPlaybackBridge(
    private val spotifyManager: SpotifyManager,
    private val spotifyApi: SpotifyApi,
    preferencesManager: UserPreferencesManager,
) : SpotifyPlaybackBridge {

    companion object {
        private const val TAG = "SpotifyBridge"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override val isConnected: StateFlow<Boolean> = spotifyManager.connectionStatus
        .map { it == SpotifyConnectionStatus.CONNECTED }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    override val isLinked: StateFlow<Boolean> = preferencesManager.spotifyLinked
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    override val playerState: StateFlow<SpotifyBridgePlayerState> = spotifyManager.playerState
        .map { state ->
            SpotifyBridgePlayerState(
                isPlaying = !state.isPaused,
                trackName = state.trackName,
                artistName = state.artistName,
                isPaused = state.isPaused,
                positionMs = state.positionMs,
                durationMs = state.durationMs,
            )
        }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), SpotifyBridgePlayerState())

    override suspend fun searchAndPlay(query: String, title: String, titleTelugu: String): Boolean {
        Log.d(TAG, "searchAndPlay: query='$query', title='$title'")

        // Ensure token is valid
        val tokenValid = spotifyManager.ensureTokenValid()
        if (!tokenValid) {
            Log.w(TAG, "Token not valid, cannot play")
            return false
        }

        val accessToken = spotifyManager.getAccessToken() ?: return false

        return try {
            val response = spotifyApi.searchTracks(
                authorization = "Bearer $accessToken",
                query = query,
            )

            val bestMatch = response.tracks?.items?.firstOrNull()
            if (bestMatch != null) {
                Log.d(TAG, "Found track: ${bestMatch.name} by ${bestMatch.artists.firstOrNull()?.name}, uri=${bestMatch.uri}")

                // Ensure connected then play
                spotifyManager.connectAppRemote(showAuth = false)

                // Wait for connection
                spotifyManager.connectionStatus.first {
                    it == SpotifyConnectionStatus.CONNECTED || it == SpotifyConnectionStatus.ERROR
                }

                if (spotifyManager.connectionStatus.value == SpotifyConnectionStatus.CONNECTED) {
                    spotifyManager.play(bestMatch.uri)
                    true
                } else {
                    Log.w(TAG, "Failed to connect AppRemote for playback")
                    false
                }
            } else {
                Log.w(TAG, "No matching track found on Spotify for: $query")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Spotify search/play error: ${e.message}", e)
            false
        }
    }

    override fun pause() {
        spotifyManager.pause()
    }

    override fun resume() {
        spotifyManager.resume()
    }

    override fun stop() {
        spotifyManager.pause()
    }
}
