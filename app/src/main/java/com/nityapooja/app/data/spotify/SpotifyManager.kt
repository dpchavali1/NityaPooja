package com.nityapooja.app.data.spotify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.nityapooja.app.BuildConfig
import com.nityapooja.app.data.preferences.UserPreferencesManager
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

enum class SpotifyConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR,
}

data class SpotifyPlayerState(
    val trackName: String = "",
    val artistName: String = "",
    val albumImageUrl: String = "",
    val isPaused: Boolean = true,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
)

@Singleton
class SpotifyManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: UserPreferencesManager,
) {
    companion object {
        const val AUTH_REQUEST_CODE = 1337
        private const val CLIENT_ID = BuildConfig.SPOTIFY_CLIENT_ID
        private const val REDIRECT_URI = BuildConfig.SPOTIFY_REDIRECT_URI
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 3

    private val _connectionStatus = MutableStateFlow(SpotifyConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<SpotifyConnectionStatus> = _connectionStatus.asStateFlow()

    private val _playerState = MutableStateFlow(SpotifyPlayerState())
    val playerState: StateFlow<SpotifyPlayerState> = _playerState.asStateFlow()

    private var accessToken: String? = null

    init {
        // Auto-reconnect App Remote on app startup if previously linked
        scope.launch {
            val tokenValid = ensureTokenValid()
            if (tokenValid && isSpotifyInstalled()) {
                connectAppRemote()
            }
        }
    }

    fun isSpotifyInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.spotify.music", 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun startAuth(activity: Activity) {
        val request = AuthorizationRequest.Builder(
            CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            REDIRECT_URI,
        )
            .setScopes(arrayOf("app-remote-control", "streaming", "user-read-playback-state", "user-modify-playback-state"))
            .build()

        AuthorizationClient.openLoginActivity(activity, AUTH_REQUEST_CODE, request)
    }

    fun handleAuthResponse(resultCode: Int, data: Intent?) {
        val response = AuthorizationClient.getResponse(resultCode, data)
        when (response.type) {
            AuthorizationResponse.Type.TOKEN -> {
                accessToken = response.accessToken
                scope.launch {
                    preferencesManager.setSpotifyToken(
                        response.accessToken,
                        response.expiresIn,
                    )
                }
                connectAppRemote(showAuth = false)
            }
            AuthorizationResponse.Type.ERROR -> {
                _connectionStatus.value = SpotifyConnectionStatus.ERROR
            }
            else -> {
                _connectionStatus.value = SpotifyConnectionStatus.DISCONNECTED
            }
        }
    }

    fun connectAppRemote(showAuth: Boolean = false) {
        if (spotifyAppRemote?.isConnected == true) {
            _connectionStatus.value = SpotifyConnectionStatus.CONNECTED
            reconnectAttempts = 0
            return
        }

        if (!isSpotifyInstalled()) {
            _connectionStatus.value = SpotifyConnectionStatus.ERROR
            return
        }

        _connectionStatus.value = SpotifyConnectionStatus.CONNECTING

        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(showAuth)
            .build()

        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                _connectionStatus.value = SpotifyConnectionStatus.CONNECTED
                reconnectAttempts = 0
                subscribeToPlayerState()
                checkUserCapabilities()
            }

            override fun onFailure(throwable: Throwable) {
                spotifyAppRemote = null
                if (reconnectAttempts < maxReconnectAttempts) {
                    reconnectAttempts++
                    scope.launch {
                        kotlinx.coroutines.delay(1000L * reconnectAttempts)
                        connectAppRemote(showAuth = false)
                    }
                } else {
                    reconnectAttempts = 0
                    _connectionStatus.value = SpotifyConnectionStatus.ERROR
                }
            }
        })
    }

    private fun subscribeToPlayerState() {
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()
            ?.setEventCallback { state ->
                val track = state.track
                _playerState.update {
                    SpotifyPlayerState(
                        trackName = track?.name ?: "",
                        artistName = track?.artist?.name ?: "",
                        albumImageUrl = track?.imageUri?.raw ?: "",
                        isPaused = state.isPaused,
                        positionMs = state.playbackPosition,
                        durationMs = track?.duration ?: 0L,
                    )
                }
            }
            ?.setErrorCallback {
                // Connection dropped — attempt silent reconnect
                spotifyAppRemote = null
                _connectionStatus.value = SpotifyConnectionStatus.DISCONNECTED
                scope.launch {
                    val tokenValid = ensureTokenValid()
                    if (tokenValid && isSpotifyInstalled()) {
                        connectAppRemote(showAuth = false)
                    }
                }
            }
    }

    private val _isPremiumUser = MutableStateFlow<Boolean?>(null)
    val isPremiumUser: StateFlow<Boolean?> = _isPremiumUser.asStateFlow()

    fun checkUserCapabilities() {
        spotifyAppRemote?.userApi?.capabilities?.setResultCallback { capabilities ->
            _isPremiumUser.value = capabilities.canPlayOnDemand
        }
    }

    fun play(spotifyUri: String) {
        if (spotifyAppRemote?.isConnected == true) {
            spotifyAppRemote?.playerApi?.play(spotifyUri)
        } else {
            // Reconnect then play
            val pendingUri = spotifyUri
            scope.launch {
                connectAppRemote(showAuth = false)
                connectionStatus.first { it == SpotifyConnectionStatus.CONNECTED || it == SpotifyConnectionStatus.ERROR }
                if (_connectionStatus.value == SpotifyConnectionStatus.CONNECTED) {
                    spotifyAppRemote?.playerApi?.play(pendingUri)
                }
            }
        }
    }

    fun pause() {
        spotifyAppRemote?.playerApi?.pause()
    }

    fun resume() {
        if (spotifyAppRemote?.isConnected == true) {
            spotifyAppRemote?.playerApi?.resume()
        } else {
            scope.launch {
                connectAppRemote(showAuth = false)
                connectionStatus.first { it == SpotifyConnectionStatus.CONNECTED || it == SpotifyConnectionStatus.ERROR }
                if (_connectionStatus.value == SpotifyConnectionStatus.CONNECTED) {
                    spotifyAppRemote?.playerApi?.resume()
                }
            }
        }
    }

    fun disconnect() {
        spotifyAppRemote?.let { SpotifyAppRemote.disconnect(it) }
        spotifyAppRemote = null
        _connectionStatus.value = SpotifyConnectionStatus.DISCONNECTED
        _playerState.value = SpotifyPlayerState()
        scope.launch {
            preferencesManager.clearSpotifyToken()
        }
    }

    fun getAccessToken(): String? = accessToken

    suspend fun ensureTokenValid(): Boolean {
        val isLinked = preferencesManager.spotifyLinked.first()
        if (!isLinked) return false

        val tokenExpiry = preferencesManager.spotifyTokenExpiry.first()
        if (System.currentTimeMillis() >= tokenExpiry) {
            // Token expired, need re-auth
            _connectionStatus.value = SpotifyConnectionStatus.DISCONNECTED
            return false
        }

        val token = preferencesManager.spotifyAccessToken.first()
        if (token.isNotBlank()) {
            accessToken = token
            return true
        }
        return false
    }

    fun openSpotifyPlayStore() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.spotify.music")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
