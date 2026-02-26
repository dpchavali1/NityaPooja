package com.nityapooja.shared.data.spotify

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class SpotifyApi(
    private val httpClient: HttpClient,
    private val credentials: SpotifyCredentials,
) {

    private var cachedToken: String? = null
    private var tokenExpiresAt: Long = 0L

    /**
     * Get an access token via Spotify Client Credentials flow.
     * This provides app-level access (search, preview URLs) without user login.
     */
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getClientCredentialsToken(): String? {
        // Return cached token if still valid (with 60s buffer)
        val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        if (cachedToken != null && now < tokenExpiresAt - 60_000) {
            return cachedToken
        }

        return try {
            val credStr = "${credentials.clientId}:${credentials.clientSecret}"
            val encoded = Base64.encode(credStr.encodeToByteArray())

            val response: SpotifyTokenResponse = httpClient.submitForm(
                url = "https://accounts.spotify.com/api/token",
                formParameters = parameters {
                    append("grant_type", "client_credentials")
                },
            ) {
                header(HttpHeaders.Authorization, "Basic $encoded")
            }.body()

            cachedToken = response.accessToken
            tokenExpiresAt = now + (response.expiresIn * 1000L)
            response.accessToken
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchTracks(
        authorization: String,
        query: String,
        type: String = "track",
        market: String = "IN",
        limit: Int = 5,
    ): SpotifySearchResponse {
        return httpClient.get("https://api.spotify.com/v1/search") {
            header(HttpHeaders.Authorization, authorization)
            parameter("q", query)
            parameter("type", type)
            parameter("market", market)
            parameter("limit", limit)
        }.body()
    }

    /**
     * Search tracks using Client Credentials (no user login needed).
     * Suitable for iOS/Desktop where Spotify SDK is unavailable.
     */
    suspend fun searchTracksWithClientCredentials(
        query: String,
        market: String = "IN",
        limit: Int = 5,
    ): SpotifySearchResponse? {
        val token = getClientCredentialsToken() ?: return null
        return searchTracks(
            authorization = "Bearer $token",
            query = query,
            market = market,
            limit = limit,
        )
    }
}

@Serializable
data class SpotifyTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
)

@Serializable
data class SpotifySearchResponse(
    @SerialName("tracks") val tracks: SpotifyTracksResult? = null,
)

@Serializable
data class SpotifyTracksResult(
    @SerialName("items") val items: List<SpotifyTrackItem>,
    @SerialName("total") val total: Int,
)

@Serializable
data class SpotifyTrackItem(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("uri") val uri: String,
    @SerialName("duration_ms") val durationMs: Long,
    @SerialName("artists") val artists: List<SpotifyArtist>,
    @SerialName("album") val album: SpotifyAlbum,
    @SerialName("preview_url") val previewUrl: String? = null,
)

@Serializable
data class SpotifyArtist(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
)

@Serializable
data class SpotifyAlbum(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("images") val images: List<SpotifyImage>,
)

@Serializable
data class SpotifyImage(
    @SerialName("url") val url: String,
    @SerialName("height") val height: Int? = null,
    @SerialName("width") val width: Int? = null,
)
