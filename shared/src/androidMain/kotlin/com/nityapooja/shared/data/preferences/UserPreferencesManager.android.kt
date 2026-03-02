package com.nityapooja.shared.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

actual class UserPreferencesManager(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val USER_NAME = stringPreferencesKey("user_name")
        val LOCATION_CITY = stringPreferencesKey("location_city")
        val LOCATION_LAT = doublePreferencesKey("location_lat")
        val LOCATION_LNG = doublePreferencesKey("location_lng")
        val LOCATION_TIMEZONE = stringPreferencesKey("location_timezone")
        val MORNING_NOTIFICATION = booleanPreferencesKey("morning_notification")
        val EVENING_NOTIFICATION = booleanPreferencesKey("evening_notification")
        val FONT_SIZE = intPreferencesKey("font_size")
        val AUTO_DARK_MODE = booleanPreferencesKey("auto_dark_mode")
        val PANCHANG_NOTIFICATIONS = booleanPreferencesKey("panchang_notifications")
        val QUIZ_NOTIFICATION = booleanPreferencesKey("quiz_notification")
        val QUIZ_NOTIFICATION_HOUR = intPreferencesKey("quiz_notification_hour")
        val QUIZ_NOTIFICATION_MINUTE = intPreferencesKey("quiz_notification_minute")
        val GRAHANAM_NOTIFICATION = booleanPreferencesKey("grahanam_notification")
        val JAPA_TARGET_MALAS = intPreferencesKey("japa_target_malas")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val GOTRA = stringPreferencesKey("gotra")
        val NAKSHATRA = stringPreferencesKey("nakshatra")
        val SPOTIFY_LINKED = booleanPreferencesKey("spotify_linked")
    }

    private val encryptedPrefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "secure_tokens",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    actual val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.THEME_MODE]) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            "saffron" -> ThemeMode.SAFFRON
            else -> ThemeMode.SYSTEM
        }
    }

    actual val fontSize: Flow<Int> = context.dataStore.data.map { prefs -> prefs[Keys.FONT_SIZE] ?: 18 }
    actual val userName: Flow<String> = context.dataStore.data.map { prefs -> prefs[Keys.USER_NAME] ?: "" }
    actual val gotra: Flow<String> = context.dataStore.data.map { prefs -> prefs[Keys.GOTRA] ?: "" }
    actual val nakshatra: Flow<String> = context.dataStore.data.map { prefs -> prefs[Keys.NAKSHATRA] ?: "" }
    actual val locationCity: Flow<String> = context.dataStore.data.map { prefs -> prefs[Keys.LOCATION_CITY] ?: "Hyderabad" }
    actual val locationLat: Flow<Double> = context.dataStore.data.map { prefs -> prefs[Keys.LOCATION_LAT] ?: 17.385 }
    actual val locationLng: Flow<Double> = context.dataStore.data.map { prefs -> prefs[Keys.LOCATION_LNG] ?: 78.4867 }
    actual val locationTimezone: Flow<String> = context.dataStore.data.map { prefs -> prefs[Keys.LOCATION_TIMEZONE] ?: "Asia/Kolkata" }
    actual val morningNotification: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[Keys.MORNING_NOTIFICATION] ?: true }
    actual val eveningNotification: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[Keys.EVENING_NOTIFICATION] ?: true }
    actual val autoDarkMode: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[Keys.AUTO_DARK_MODE] ?: false }
    actual val panchangNotifications: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[Keys.PANCHANG_NOTIFICATIONS] ?: false }
    actual val quizNotification: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[Keys.QUIZ_NOTIFICATION] ?: true }
    actual val quizNotificationHour: Flow<Int> = context.dataStore.data.map { prefs -> prefs[Keys.QUIZ_NOTIFICATION_HOUR] ?: 19 }
    actual val quizNotificationMinute: Flow<Int> = context.dataStore.data.map { prefs -> prefs[Keys.QUIZ_NOTIFICATION_MINUTE] ?: 30 }
    actual val grahanamNotification: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[Keys.GRAHANAM_NOTIFICATION] ?: true }
    actual val japaTargetMalas: Flow<Int> = context.dataStore.data.map { prefs -> prefs[Keys.JAPA_TARGET_MALAS] ?: 3 }
    actual val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[Keys.ONBOARDING_COMPLETED] ?: false }
    actual val spotifyLinked: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[Keys.SPOTIFY_LINKED] ?: false }
    actual val spotifyAccessToken: Flow<String> = context.dataStore.data.map { encryptedPrefs.getString("spotify_access_token", "") ?: "" }
    actual val spotifyTokenExpiry: Flow<Long> = context.dataStore.data.map { encryptedPrefs.getLong("spotify_token_expiry", 0L) }

    actual suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = when (mode) {
                ThemeMode.SYSTEM -> "system"
                ThemeMode.LIGHT -> "light"
                ThemeMode.DARK -> "dark"
                ThemeMode.SAFFRON -> "saffron"
            }
        }
    }
    actual suspend fun setFontSize(size: Int) { context.dataStore.edit { it[Keys.FONT_SIZE] = size.coerceIn(14, 32) } }
    actual suspend fun setUserName(name: String) { context.dataStore.edit { it[Keys.USER_NAME] = name } }
    actual suspend fun setGotra(gotra: String) { context.dataStore.edit { it[Keys.GOTRA] = gotra } }
    actual suspend fun setNakshatra(nakshatra: String) { context.dataStore.edit { it[Keys.NAKSHATRA] = nakshatra } }
    actual suspend fun setLocation(city: String, lat: Double, lng: Double, timezone: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LOCATION_CITY] = city
            prefs[Keys.LOCATION_LAT] = lat
            prefs[Keys.LOCATION_LNG] = lng
            prefs[Keys.LOCATION_TIMEZONE] = timezone
        }
    }
    actual suspend fun setMorningNotification(enabled: Boolean) { context.dataStore.edit { it[Keys.MORNING_NOTIFICATION] = enabled } }
    actual suspend fun setEveningNotification(enabled: Boolean) { context.dataStore.edit { it[Keys.EVENING_NOTIFICATION] = enabled } }
    actual suspend fun setAutoDarkMode(enabled: Boolean) { context.dataStore.edit { it[Keys.AUTO_DARK_MODE] = enabled } }
    actual suspend fun setPanchangNotifications(enabled: Boolean) { context.dataStore.edit { it[Keys.PANCHANG_NOTIFICATIONS] = enabled } }
    actual suspend fun setQuizNotification(enabled: Boolean) { context.dataStore.edit { it[Keys.QUIZ_NOTIFICATION] = enabled } }
    actual suspend fun setGrahanamNotification(enabled: Boolean) { context.dataStore.edit { it[Keys.GRAHANAM_NOTIFICATION] = enabled } }
    actual suspend fun setQuizNotificationTime(hour: Int, minute: Int) {
        context.dataStore.edit { it[Keys.QUIZ_NOTIFICATION_HOUR] = hour; it[Keys.QUIZ_NOTIFICATION_MINUTE] = minute }
    }
    actual suspend fun setJapaTargetMalas(target: Int) { context.dataStore.edit { it[Keys.JAPA_TARGET_MALAS] = target } }
    actual suspend fun setOnboardingCompleted(completed: Boolean) { context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed } }
    actual suspend fun setSpotifyToken(token: String, expiresIn: Int) {
        encryptedPrefs.edit()
            .putString("spotify_access_token", token)
            .putLong("spotify_token_expiry", kotlinx.datetime.Clock.System.now().toEpochMilliseconds() + (expiresIn * 1000L))
            .apply()
        context.dataStore.edit { it[Keys.SPOTIFY_LINKED] = true }
    }
    actual suspend fun clearSpotifyToken() {
        encryptedPrefs.edit().remove("spotify_access_token").remove("spotify_token_expiry").apply()
        context.dataStore.edit { it[Keys.SPOTIFY_LINKED] = false }
    }
    actual suspend fun clearAllPreferences() {
        context.dataStore.edit { it.clear() }
        encryptedPrefs.edit().clear().apply()
    }

    actual suspend fun getSeededVersion(): Int {
        val prefs = context.getSharedPreferences("nityapooja_internal", Context.MODE_PRIVATE)
        return prefs.getInt("seeded_version", 0)
    }

    actual suspend fun setSeededVersion(version: Int) {
        val prefs = context.getSharedPreferences("nityapooja_internal", Context.MODE_PRIVATE)
        prefs.edit().putInt("seeded_version", version).apply()
    }
}
