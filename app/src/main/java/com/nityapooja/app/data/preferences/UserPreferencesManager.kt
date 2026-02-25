package com.nityapooja.app.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

enum class ThemeMode { SYSTEM, LIGHT, DARK, SAFFRON }

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
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
        val JAPA_TARGET_MALAS = intPreferencesKey("japa_target_malas")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val GOTRA = stringPreferencesKey("gotra")
        val NAKSHATRA = stringPreferencesKey("nakshatra")
        val SPOTIFY_LINKED = booleanPreferencesKey("spotify_linked")
    }

    // Encrypted storage for OAuth tokens
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

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.THEME_MODE]) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            "saffron" -> ThemeMode.SAFFRON
            else -> ThemeMode.SYSTEM
        }
    }

    val fontSize: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.FONT_SIZE] ?: 16  // default 16sp
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.USER_NAME] ?: ""
    }

    val gotra: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.GOTRA] ?: ""
    }

    val nakshatra: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.NAKSHATRA] ?: ""
    }

    val locationCity: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.LOCATION_CITY] ?: "Hyderabad"
    }

    val locationLat: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[Keys.LOCATION_LAT] ?: 17.385
    }

    val locationLng: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[Keys.LOCATION_LNG] ?: 78.4867
    }

    val locationTimezone: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.LOCATION_TIMEZONE] ?: "Asia/Kolkata"
    }

    val morningNotification: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.MORNING_NOTIFICATION] ?: true
    }

    val eveningNotification: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.EVENING_NOTIFICATION] ?: true
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = when (mode) {
                ThemeMode.SYSTEM -> "system"
                ThemeMode.LIGHT -> "light"
                ThemeMode.DARK -> "dark"
                ThemeMode.SAFFRON -> "saffron"
            }
        }
    }

    suspend fun setFontSize(size: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FONT_SIZE] = size.coerceIn(12, 28)
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name
        }
    }

    suspend fun setGotra(gotra: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GOTRA] = gotra
        }
    }

    suspend fun setNakshatra(nakshatra: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NAKSHATRA] = nakshatra
        }
    }

    suspend fun setLocation(city: String, lat: Double, lng: Double, timezone: String = "Asia/Kolkata") {
        context.dataStore.edit { prefs ->
            prefs[Keys.LOCATION_CITY] = city
            prefs[Keys.LOCATION_LAT] = lat
            prefs[Keys.LOCATION_LNG] = lng
            prefs[Keys.LOCATION_TIMEZONE] = timezone
        }
    }

    suspend fun setMorningNotification(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.MORNING_NOTIFICATION] = enabled
        }
    }

    suspend fun setEveningNotification(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.EVENING_NOTIFICATION] = enabled
        }
    }

    val autoDarkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.AUTO_DARK_MODE] ?: false
    }

    suspend fun setAutoDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.AUTO_DARK_MODE] = enabled
        }
    }

    val panchangNotifications: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.PANCHANG_NOTIFICATIONS] ?: false
    }

    suspend fun setPanchangNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.PANCHANG_NOTIFICATIONS] = enabled
        }
    }

    val japaTargetMalas: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.JAPA_TARGET_MALAS] ?: 3
    }

    suspend fun setJapaTargetMalas(target: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.JAPA_TARGET_MALAS] = target
        }
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    // === SPOTIFY (tokens stored in encrypted storage) ===
    val spotifyLinked: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SPOTIFY_LINKED] ?: false
    }

    val spotifyAccessToken: Flow<String> = context.dataStore.data.map {
        encryptedPrefs.getString("spotify_access_token", "") ?: ""
    }

    val spotifyTokenExpiry: Flow<Long> = context.dataStore.data.map {
        encryptedPrefs.getLong("spotify_token_expiry", 0L)
    }

    suspend fun setSpotifyToken(token: String, expiresIn: Int) {
        encryptedPrefs.edit()
            .putString("spotify_access_token", token)
            .putLong("spotify_token_expiry", System.currentTimeMillis() + (expiresIn * 1000L))
            .apply()
        context.dataStore.edit { prefs ->
            prefs[Keys.SPOTIFY_LINKED] = true
        }
    }

    suspend fun clearSpotifyToken() {
        encryptedPrefs.edit()
            .remove("spotify_access_token")
            .remove("spotify_token_expiry")
            .apply()
        context.dataStore.edit { prefs ->
            prefs[Keys.SPOTIFY_LINKED] = false
        }
    }

    suspend fun clearAllPreferences() {
        context.dataStore.edit { it.clear() }
        encryptedPrefs.edit().clear().apply()
    }
}
