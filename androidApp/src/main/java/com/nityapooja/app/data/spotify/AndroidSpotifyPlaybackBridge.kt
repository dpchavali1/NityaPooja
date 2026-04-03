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

        return try {
            // Use client credentials for search — no user token needed, never expires
            val response = spotifyApi.searchTracksWithClientCredentials(query = query)

            if (response == null) {
                Log.w(TAG, "Client credentials token failed, falling back to user token")
                // Fallback: try user token if client credentials fail
                val tokenValid = spotifyManager.ensureTokenValid()
                if (!tokenValid) {
                    Log.w(TAG, "No valid token available")
                    return false
                }
                val accessToken = spotifyManager.getAccessToken() ?: return false
                val fallbackResponse = spotifyApi.searchTracks(
                    authorization = "Bearer $accessToken",
                    query = query,
                )
                playFirstMatch(fallbackResponse)
            } else {
                playFirstMatch(response)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Spotify search/play error: ${e.message}", e)
            false
        }
    }

    private suspend fun playFirstMatch(response: com.nityapooja.shared.data.spotify.SpotifySearchResponse): Boolean {
        val bestMatch = response.tracks?.items?.firstOrNull()
        if (bestMatch != null) {
            Log.d(TAG, "Found track: ${bestMatch.name} by ${bestMatch.artists.firstOrNull()?.name}, uri=${bestMatch.uri}")

            // AppRemote authenticates via Spotify app, not OAuth token
            if (spotifyManager.connectionStatus.value != SpotifyConnectionStatus.CONNECTED) {
                spotifyManager.connectAppRemote(showAuth = false)
                // Wait up to 5 seconds for connection
                kotlinx.coroutines.withTimeoutOrNull(5000L) {
                    spotifyManager.connectionStatus.first {
                        it == SpotifyConnectionStatus.CONNECTED || it == SpotifyConnectionStatus.ERROR
                    }
                }
            }

            if (spotifyManager.connectionStatus.value == SpotifyConnectionStatus.CONNECTED) {
                spotifyManager.play(bestMatch.uri)
                return true
            } else {
                Log.w(TAG, "Failed to connect AppRemote for playback")
                return false
            }
        } else {
            Log.w(TAG, "No matching track found on Spotify")
            return false
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
