package com.nityapooja.app.data.spotify

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyWebApi {

    @GET("v1/search")
    suspend fun searchTracks(
        @Header("Authorization") authorization: String,
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("market") market: String = "IN",
        @Query("limit") limit: Int = 5,
    ): SpotifySearchResponse
}

data class SpotifySearchResponse(
    @SerializedName("tracks") val tracks: SpotifyTracksResult?,
)

data class SpotifyTracksResult(
    @SerializedName("items") val items: List<SpotifyTrackItem>,
    @SerializedName("total") val total: Int,
)

data class SpotifyTrackItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("duration_ms") val durationMs: Long,
    @SerializedName("artists") val artists: List<SpotifyArtist>,
    @SerializedName("album") val album: SpotifyAlbum,
    @SerializedName("preview_url") val previewUrl: String?,
)

data class SpotifyArtist(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
)

data class SpotifyAlbum(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("images") val images: List<SpotifyImage>,
)

data class SpotifyImage(
    @SerializedName("url") val url: String,
    @SerializedName("height") val height: Int?,
    @SerializedName("width") val width: Int?,
)
