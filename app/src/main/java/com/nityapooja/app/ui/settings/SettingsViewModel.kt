package com.nityapooja.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.local.dao.BookmarkDao
import com.nityapooja.app.data.local.dao.JapaSessionDao
import com.nityapooja.app.data.local.dao.ReadingHistoryDao
import com.nityapooja.app.data.preferences.ThemeMode
import com.nityapooja.app.data.preferences.UserPreferencesManager
import com.nityapooja.app.data.spotify.SpotifyConnectionStatus
import com.nityapooja.app.data.spotify.SpotifyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager,
    private val bookmarkDao: BookmarkDao,
    private val japaSessionDao: JapaSessionDao,
    private val readingHistoryDao: ReadingHistoryDao,
    private val spotifyManager: SpotifyManager,
) : ViewModel() {

    private val _dataCleared = MutableStateFlow(false)
    val dataCleared: StateFlow<Boolean> = _dataCleared.asStateFlow()

    val spotifyLinked: StateFlow<Boolean> = preferencesManager.spotifyLinked
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val spotifyConnectionStatus: StateFlow<SpotifyConnectionStatus> = spotifyManager.connectionStatus
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SpotifyConnectionStatus.DISCONNECTED)

    private val _spotifyInstalled = MutableStateFlow(spotifyManager.isSpotifyInstalled())
    val spotifyInstalled: StateFlow<Boolean> = _spotifyInstalled.asStateFlow()

    val themeMode: StateFlow<ThemeMode> = preferencesManager.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val userName: StateFlow<String> = preferencesManager.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val gotra: StateFlow<String> = preferencesManager.gotra
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val nakshatra: StateFlow<String> = preferencesManager.nakshatra
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val locationCity: StateFlow<String> = preferencesManager.locationCity
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Hyderabad")

    val morningNotification: StateFlow<Boolean> = preferencesManager.morningNotification
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val eveningNotification: StateFlow<Boolean> = preferencesManager.eveningNotification
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val fontSize: StateFlow<Int> = preferencesManager.fontSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 16)

    val autoDarkMode: StateFlow<Boolean> = preferencesManager.autoDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val panchangNotifications: StateFlow<Boolean> = preferencesManager.panchangNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferencesManager.setThemeMode(mode) }
    }

    fun setUserName(name: String) {
        viewModelScope.launch { preferencesManager.setUserName(name) }
    }

    fun setGotra(gotra: String) {
        viewModelScope.launch { preferencesManager.setGotra(gotra) }
    }

    fun setNakshatra(nakshatra: String) {
        viewModelScope.launch { preferencesManager.setNakshatra(nakshatra) }
    }

    fun setLocation(city: String, lat: Double, lng: Double, timezone: String = "Asia/Kolkata") {
        viewModelScope.launch { preferencesManager.setLocation(city, lat, lng, timezone) }
    }

    fun setMorningNotification(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setMorningNotification(enabled) }
    }

    fun setEveningNotification(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setEveningNotification(enabled) }
    }

    fun setFontSize(size: Int) {
        viewModelScope.launch { preferencesManager.setFontSize(size) }
    }

    fun setAutoDarkMode(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setAutoDarkMode(enabled) }
    }

    fun setPanchangNotifications(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setPanchangNotifications(enabled) }
    }

    fun clearAllUserData() {
        viewModelScope.launch {
            bookmarkDao.clearAll()
            japaSessionDao.clearAll()
            readingHistoryDao.clearAll()
            preferencesManager.clearAllPreferences()
            _dataCleared.value = true
        }
    }

    fun resetDataClearedFlag() {
        _dataCleared.value = false
    }

    // === SPOTIFY ===
    fun linkSpotify() {
        // This needs an Activity reference — handled via a callback from the UI
        // The actual auth flow is triggered from MainActivity
        spotifyManager.connectAppRemote()
    }

    fun unlinkSpotify() {
        spotifyManager.disconnect()
    }

    fun openSpotifyPlayStore() {
        spotifyManager.openSpotifyPlayStore()
    }

    fun getSpotifyManager(): SpotifyManager = spotifyManager
}
