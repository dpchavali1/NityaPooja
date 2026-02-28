package com.nityapooja.shared.data.preferences

import kotlinx.coroutines.flow.Flow

enum class ThemeMode { SYSTEM, LIGHT, DARK, SAFFRON }

expect class UserPreferencesManager {
    val themeMode: Flow<ThemeMode>
    val fontSize: Flow<Int>
    val userName: Flow<String>
    val gotra: Flow<String>
    val nakshatra: Flow<String>
    val locationCity: Flow<String>
    val locationLat: Flow<Double>
    val locationLng: Flow<Double>
    val locationTimezone: Flow<String>
    val morningNotification: Flow<Boolean>
    val eveningNotification: Flow<Boolean>
    val autoDarkMode: Flow<Boolean>
    val panchangNotifications: Flow<Boolean>
    val quizNotification: Flow<Boolean>
    val quizNotificationHour: Flow<Int>
    val quizNotificationMinute: Flow<Int>
    val japaTargetMalas: Flow<Int>
    val onboardingCompleted: Flow<Boolean>
    val spotifyLinked: Flow<Boolean>
    val spotifyAccessToken: Flow<String>
    val spotifyTokenExpiry: Flow<Long>

    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setFontSize(size: Int)
    suspend fun setUserName(name: String)
    suspend fun setGotra(gotra: String)
    suspend fun setNakshatra(nakshatra: String)
    suspend fun setLocation(city: String, lat: Double, lng: Double, timezone: String)
    suspend fun setMorningNotification(enabled: Boolean)
    suspend fun setEveningNotification(enabled: Boolean)
    suspend fun setAutoDarkMode(enabled: Boolean)
    suspend fun setPanchangNotifications(enabled: Boolean)
    suspend fun setQuizNotification(enabled: Boolean)
    suspend fun setQuizNotificationTime(hour: Int, minute: Int)
    suspend fun setJapaTargetMalas(target: Int)
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setSpotifyToken(token: String, expiresIn: Int)
    suspend fun clearSpotifyToken()
    suspend fun clearAllPreferences()

    suspend fun getSeededVersion(): Int
    suspend fun setSeededVersion(version: Int)
}
