package com.nityapooja.shared.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val preferencesManager: UserPreferencesManager,
) : ViewModel() {

    val onboardingCompleted: StateFlow<Boolean> = preferencesManager.onboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    suspend fun completeOnboarding(
        userName: String = "",
        city: String = "",
        lat: Double = 17.385,
        lng: Double = 78.487,
        timezone: String = "Asia/Kolkata",
        morningNotification: Boolean = true,
        eveningNotification: Boolean = true,
    ) {
        if (userName.isNotBlank()) preferencesManager.setUserName(userName)
        if (city.isNotBlank()) preferencesManager.setLocation(city, lat, lng, timezone)
        preferencesManager.setMorningNotification(morningNotification)
        preferencesManager.setEveningNotification(eveningNotification)
        preferencesManager.setOnboardingCompleted(true)
    }
}
