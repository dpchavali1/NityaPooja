package com.nityapooja.shared.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.dao.BookmarkDao
import com.nityapooja.shared.data.local.dao.JapaSessionDao
import com.nityapooja.shared.data.local.dao.ReadingHistoryDao
import com.nityapooja.shared.data.preferences.ThemeMode
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesManager: UserPreferencesManager,
    private val bookmarkDao: BookmarkDao,
    private val japaSessionDao: JapaSessionDao,
    private val readingHistoryDao: ReadingHistoryDao,
    private val notificationScheduler: com.nityapooja.shared.platform.NotificationScheduler,
) : ViewModel() {

    private val _dataCleared = MutableStateFlow(false)
    val dataCleared: StateFlow<Boolean> = _dataCleared.asStateFlow()

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

    val locationTimezone: StateFlow<String> = preferencesManager.locationTimezone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Asia/Kolkata")

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

    fun setThemeMode(mode: ThemeMode) { viewModelScope.launch { preferencesManager.setThemeMode(mode) } }
    fun setUserName(name: String) { viewModelScope.launch { preferencesManager.setUserName(name) } }
    fun setGotra(gotra: String) { viewModelScope.launch { preferencesManager.setGotra(gotra) } }
    fun setNakshatra(nakshatra: String) { viewModelScope.launch { preferencesManager.setNakshatra(nakshatra) } }
    fun setLocation(city: String, lat: Double, lng: Double, timezone: String = "Asia/Kolkata") {
        viewModelScope.launch { preferencesManager.setLocation(city, lat, lng, timezone) }
        // Reschedule notifications with the new timezone
        rescheduleNotifications(timezone)
    }
    fun setMorningNotification(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setMorningNotification(enabled) }
        if (enabled) notificationScheduler.scheduleMorningReminder(5, 30, locationTimezone.value)
        else notificationScheduler.cancelMorningReminder()
    }
    fun setEveningNotification(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setEveningNotification(enabled) }
        if (enabled) notificationScheduler.scheduleEveningReminder(18, 30, locationTimezone.value)
        else notificationScheduler.cancelEveningReminder()
    }
    fun setFontSize(size: Int) { viewModelScope.launch { preferencesManager.setFontSize(size) } }
    fun setAutoDarkMode(enabled: Boolean) { viewModelScope.launch { preferencesManager.setAutoDarkMode(enabled) } }
    fun setPanchangNotifications(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setPanchangNotifications(enabled) }
        if (enabled) notificationScheduler.schedulePanchangReminder(locationTimezone.value)
        else notificationScheduler.cancelPanchangReminder()
    }

    /** Re-schedule all active notifications when timezone changes */
    private fun rescheduleNotifications(timezone: String) {
        if (morningNotification.value) {
            notificationScheduler.scheduleMorningReminder(5, 30, timezone)
        }
        if (eveningNotification.value) {
            notificationScheduler.scheduleEveningReminder(18, 30, timezone)
        }
        if (panchangNotifications.value) {
            notificationScheduler.schedulePanchangReminder(timezone)
        }
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

    fun resetDataClearedFlag() { _dataCleared.value = false }
}
