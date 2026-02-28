package com.nityapooja.shared.data.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences

actual class UserPreferencesManager {
    private val prefs = Preferences.userRoot().node("com/nityapooja")

    // Backing state flows — setters update these so UI reacts immediately
    private val _themeMode = MutableStateFlow(prefs.get("theme_mode", "system"))
    private val _fontSize = MutableStateFlow(prefs.getInt("font_size", 18))
    private val _userName = MutableStateFlow(prefs.get("user_name", ""))
    private val _gotra = MutableStateFlow(prefs.get("gotra", ""))
    private val _nakshatra = MutableStateFlow(prefs.get("nakshatra", ""))
    private val _locationCity = MutableStateFlow(prefs.get("location_city", "Hyderabad"))
    private val _locationLat = MutableStateFlow(prefs.getDouble("location_lat", 17.385))
    private val _locationLng = MutableStateFlow(prefs.getDouble("location_lng", 78.4867))
    private val _locationTimezone = MutableStateFlow(prefs.get("location_timezone", "Asia/Kolkata"))
    private val _morningNotification = MutableStateFlow(prefs.getBoolean("morning_notification", true))
    private val _eveningNotification = MutableStateFlow(prefs.getBoolean("evening_notification", true))
    private val _autoDarkMode = MutableStateFlow(prefs.getBoolean("auto_dark_mode", false))
    private val _panchangNotifications = MutableStateFlow(prefs.getBoolean("panchang_notifications", false))
    private val _quizNotification = MutableStateFlow(prefs.getBoolean("quiz_notification", true))
    private val _quizNotificationHour = MutableStateFlow(prefs.getInt("quiz_notification_hour", 19))
    private val _quizNotificationMinute = MutableStateFlow(prefs.getInt("quiz_notification_minute", 30))
    private val _japaTargetMalas = MutableStateFlow(prefs.getInt("japa_target_malas", 3))
    private val _onboardingCompleted = MutableStateFlow(prefs.getBoolean("onboarding_completed", false))
    private val _spotifyLinked = MutableStateFlow(prefs.getBoolean("spotify_linked", false))
    private val _spotifyAccessToken = MutableStateFlow(prefs.get("spotify_access_token", ""))
    private val _spotifyTokenExpiry = MutableStateFlow(prefs.getLong("spotify_token_expiry", 0L))

    actual val themeMode: Flow<ThemeMode> = _themeMode.map {
        when (it) { "light" -> ThemeMode.LIGHT; "dark" -> ThemeMode.DARK; "saffron" -> ThemeMode.SAFFRON; else -> ThemeMode.SYSTEM }
    }
    actual val fontSize: Flow<Int> = _fontSize
    actual val userName: Flow<String> = _userName
    actual val gotra: Flow<String> = _gotra
    actual val nakshatra: Flow<String> = _nakshatra
    actual val locationCity: Flow<String> = _locationCity
    actual val locationLat: Flow<Double> = _locationLat
    actual val locationLng: Flow<Double> = _locationLng
    actual val locationTimezone: Flow<String> = _locationTimezone
    actual val morningNotification: Flow<Boolean> = _morningNotification
    actual val eveningNotification: Flow<Boolean> = _eveningNotification
    actual val autoDarkMode: Flow<Boolean> = _autoDarkMode
    actual val panchangNotifications: Flow<Boolean> = _panchangNotifications
    actual val quizNotification: Flow<Boolean> = _quizNotification
    actual val quizNotificationHour: Flow<Int> = _quizNotificationHour
    actual val quizNotificationMinute: Flow<Int> = _quizNotificationMinute
    actual val japaTargetMalas: Flow<Int> = _japaTargetMalas
    actual val onboardingCompleted: Flow<Boolean> = _onboardingCompleted
    actual val spotifyLinked: Flow<Boolean> = _spotifyLinked
    actual val spotifyAccessToken: Flow<String> = _spotifyAccessToken
    actual val spotifyTokenExpiry: Flow<Long> = _spotifyTokenExpiry

    actual suspend fun setThemeMode(mode: ThemeMode) {
        val str = when (mode) { ThemeMode.SYSTEM -> "system"; ThemeMode.LIGHT -> "light"; ThemeMode.DARK -> "dark"; ThemeMode.SAFFRON -> "saffron" }
        prefs.put("theme_mode", str); _themeMode.value = str
    }
    actual suspend fun setFontSize(size: Int) {
        val clamped = size.coerceIn(14, 32)
        prefs.putInt("font_size", clamped); _fontSize.value = clamped
    }
    actual suspend fun setUserName(name: String) { prefs.put("user_name", name); _userName.value = name }
    actual suspend fun setGotra(gotra: String) { prefs.put("gotra", gotra); _gotra.value = gotra }
    actual suspend fun setNakshatra(nakshatra: String) { prefs.put("nakshatra", nakshatra); _nakshatra.value = nakshatra }
    actual suspend fun setLocation(city: String, lat: Double, lng: Double, timezone: String) {
        prefs.put("location_city", city); _locationCity.value = city
        prefs.putDouble("location_lat", lat); _locationLat.value = lat
        prefs.putDouble("location_lng", lng); _locationLng.value = lng
        prefs.put("location_timezone", timezone); _locationTimezone.value = timezone
    }
    actual suspend fun setMorningNotification(enabled: Boolean) { prefs.putBoolean("morning_notification", enabled); _morningNotification.value = enabled }
    actual suspend fun setEveningNotification(enabled: Boolean) { prefs.putBoolean("evening_notification", enabled); _eveningNotification.value = enabled }
    actual suspend fun setAutoDarkMode(enabled: Boolean) { prefs.putBoolean("auto_dark_mode", enabled); _autoDarkMode.value = enabled }
    actual suspend fun setPanchangNotifications(enabled: Boolean) { prefs.putBoolean("panchang_notifications", enabled); _panchangNotifications.value = enabled }
    actual suspend fun setQuizNotification(enabled: Boolean) { prefs.putBoolean("quiz_notification", enabled); _quizNotification.value = enabled }
    actual suspend fun setQuizNotificationTime(hour: Int, minute: Int) {
        prefs.putInt("quiz_notification_hour", hour); _quizNotificationHour.value = hour
        prefs.putInt("quiz_notification_minute", minute); _quizNotificationMinute.value = minute
    }
    actual suspend fun setJapaTargetMalas(target: Int) { prefs.putInt("japa_target_malas", target); _japaTargetMalas.value = target }
    actual suspend fun setOnboardingCompleted(completed: Boolean) { prefs.putBoolean("onboarding_completed", completed); _onboardingCompleted.value = completed }
    actual suspend fun setSpotifyToken(token: String, expiresIn: Int) {
        val expiry = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() + expiresIn * 1000L
        prefs.put("spotify_access_token", token); _spotifyAccessToken.value = token
        prefs.putLong("spotify_token_expiry", expiry); _spotifyTokenExpiry.value = expiry
        prefs.putBoolean("spotify_linked", true); _spotifyLinked.value = true
    }
    actual suspend fun clearSpotifyToken() {
        prefs.remove("spotify_access_token"); _spotifyAccessToken.value = ""
        prefs.remove("spotify_token_expiry"); _spotifyTokenExpiry.value = 0L
        prefs.putBoolean("spotify_linked", false); _spotifyLinked.value = false
    }
    actual suspend fun clearAllPreferences() { prefs.clear() }

    actual suspend fun getSeededVersion(): Int = prefs.getInt("seeded_version", 0)

    actual suspend fun setSeededVersion(version: Int) { prefs.putInt("seeded_version", version) }
}
