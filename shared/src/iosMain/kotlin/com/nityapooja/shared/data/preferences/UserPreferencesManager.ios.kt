package com.nityapooja.shared.data.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import platform.Foundation.NSUserDefaults

actual class UserPreferencesManager {
    private val defaults = NSUserDefaults.standardUserDefaults

    // Backing state flows — setters update these so UI reacts immediately
    private val _themeMode = MutableStateFlow(defaults.stringForKey("theme_mode") ?: "system")
    private val _fontSize = MutableStateFlow(readInt("font_size", 16))
    private val _userName = MutableStateFlow(defaults.stringForKey("user_name") ?: "")
    private val _gotra = MutableStateFlow(defaults.stringForKey("gotra") ?: "")
    private val _nakshatra = MutableStateFlow(defaults.stringForKey("nakshatra") ?: "")
    private val _locationCity = MutableStateFlow(defaults.stringForKey("location_city") ?: "Hyderabad")
    private val _locationLat = MutableStateFlow(defaults.doubleForKey("location_lat").let { if (it == 0.0 && defaults.objectForKey("location_lat") == null) 17.385 else it })
    private val _locationLng = MutableStateFlow(defaults.doubleForKey("location_lng").let { if (it == 0.0 && defaults.objectForKey("location_lng") == null) 78.4867 else it })
    private val _locationTimezone = MutableStateFlow(defaults.stringForKey("location_timezone") ?: "Asia/Kolkata")
    private val _morningNotification = MutableStateFlow(readBool("morning_notification", true))
    private val _eveningNotification = MutableStateFlow(readBool("evening_notification", true))
    private val _autoDarkMode = MutableStateFlow(readBool("auto_dark_mode", false))
    private val _panchangNotifications = MutableStateFlow(readBool("panchang_notifications", false))
    private val _japaTargetMalas = MutableStateFlow(readInt("japa_target_malas", 3))
    private val _onboardingCompleted = MutableStateFlow(readBool("onboarding_completed", false))
    private val _spotifyLinked = MutableStateFlow(readBool("spotify_linked", false))
    private val _spotifyAccessToken = MutableStateFlow(defaults.stringForKey("spotify_access_token") ?: "")
    private val _spotifyTokenExpiry = MutableStateFlow(readLong("spotify_token_expiry", 0L))

    private fun readBool(key: String, default: Boolean): Boolean =
        if (defaults.objectForKey(key) != null) defaults.boolForKey(key) else default
    private fun readInt(key: String, default: Int): Int =
        if (defaults.objectForKey(key) != null) defaults.integerForKey(key).toInt() else default
    private fun readLong(key: String, default: Long): Long =
        if (defaults.objectForKey(key) != null) defaults.integerForKey(key).toLong() else default

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
    actual val japaTargetMalas: Flow<Int> = _japaTargetMalas
    actual val onboardingCompleted: Flow<Boolean> = _onboardingCompleted
    actual val spotifyLinked: Flow<Boolean> = _spotifyLinked
    actual val spotifyAccessToken: Flow<String> = _spotifyAccessToken
    actual val spotifyTokenExpiry: Flow<Long> = _spotifyTokenExpiry

    actual suspend fun setThemeMode(mode: ThemeMode) {
        val str = when (mode) { ThemeMode.SYSTEM -> "system"; ThemeMode.LIGHT -> "light"; ThemeMode.DARK -> "dark"; ThemeMode.SAFFRON -> "saffron" }
        defaults.setObject(str, "theme_mode")
        _themeMode.value = str
    }
    actual suspend fun setFontSize(size: Int) {
        val clamped = size.coerceIn(12, 28)
        defaults.setInteger(clamped.toLong(), "font_size")
        _fontSize.value = clamped
    }
    actual suspend fun setUserName(name: String) { defaults.setObject(name, "user_name"); _userName.value = name }
    actual suspend fun setGotra(gotra: String) { defaults.setObject(gotra, "gotra"); _gotra.value = gotra }
    actual suspend fun setNakshatra(nakshatra: String) { defaults.setObject(nakshatra, "nakshatra"); _nakshatra.value = nakshatra }
    actual suspend fun setLocation(city: String, lat: Double, lng: Double, timezone: String) {
        defaults.setObject(city, "location_city"); _locationCity.value = city
        defaults.setDouble(lat, "location_lat"); _locationLat.value = lat
        defaults.setDouble(lng, "location_lng"); _locationLng.value = lng
        defaults.setObject(timezone, "location_timezone"); _locationTimezone.value = timezone
    }
    actual suspend fun setMorningNotification(enabled: Boolean) { defaults.setBool(enabled, "morning_notification"); _morningNotification.value = enabled }
    actual suspend fun setEveningNotification(enabled: Boolean) { defaults.setBool(enabled, "evening_notification"); _eveningNotification.value = enabled }
    actual suspend fun setAutoDarkMode(enabled: Boolean) { defaults.setBool(enabled, "auto_dark_mode"); _autoDarkMode.value = enabled }
    actual suspend fun setPanchangNotifications(enabled: Boolean) { defaults.setBool(enabled, "panchang_notifications"); _panchangNotifications.value = enabled }
    actual suspend fun setJapaTargetMalas(target: Int) { defaults.setInteger(target.toLong(), "japa_target_malas"); _japaTargetMalas.value = target }
    actual suspend fun setOnboardingCompleted(completed: Boolean) { defaults.setBool(completed, "onboarding_completed"); _onboardingCompleted.value = completed }
    actual suspend fun setSpotifyToken(token: String, expiresIn: Int) {
        val expiry = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() + expiresIn * 1000L
        defaults.setObject(token, "spotify_access_token"); _spotifyAccessToken.value = token
        defaults.setInteger(expiry, "spotify_token_expiry"); _spotifyTokenExpiry.value = expiry
        defaults.setBool(true, "spotify_linked"); _spotifyLinked.value = true
    }
    actual suspend fun clearSpotifyToken() {
        defaults.removeObjectForKey("spotify_access_token"); _spotifyAccessToken.value = ""
        defaults.removeObjectForKey("spotify_token_expiry"); _spotifyTokenExpiry.value = 0L
        defaults.setBool(false, "spotify_linked"); _spotifyLinked.value = false
    }
    actual suspend fun clearAllPreferences() {
        val domain = platform.Foundation.NSBundle.mainBundle.bundleIdentifier ?: return
        defaults.removePersistentDomainForName(domain)
    }

    actual suspend fun getSeededVersion(): Int =
        if (defaults.objectForKey("seeded_version") != null) defaults.integerForKey("seeded_version").toInt() else 0

    actual suspend fun setSeededVersion(version: Int) {
        defaults.setInteger(version.toLong(), "seeded_version")
    }
}
