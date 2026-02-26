package com.nityapooja.shared.data.spotify

/**
 * Holds Spotify API credentials.
 * Credentials are injected at runtime by each platform module via Koin,
 * not hardcoded in source code.
 */
data class SpotifyCredentials(
    val clientId: String,
    val clientSecret: String,
)
