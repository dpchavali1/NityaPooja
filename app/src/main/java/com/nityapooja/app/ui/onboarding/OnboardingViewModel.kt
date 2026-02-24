package com.nityapooja.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager,
) : ViewModel() {

    val onboardingCompleted: StateFlow<Boolean?> = preferencesManager.onboardingCompleted
        .map<Boolean, Boolean?> { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    suspend fun completeOnboarding(
        userName: String,
        city: String,
        lat: Double,
        lng: Double,
        timezone: String,
        morningNotification: Boolean,
        eveningNotification: Boolean,
    ) {
        if (userName.isNotBlank()) {
            preferencesManager.setUserName(userName)
        }
        preferencesManager.setLocation(city, lat, lng, timezone)
        preferencesManager.setMorningNotification(morningNotification)
        preferencesManager.setEveningNotification(eveningNotification)
        preferencesManager.setOnboardingCompleted(true)
    }
}
