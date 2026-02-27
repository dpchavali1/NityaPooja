package com.nityapooja.shared.ui.jataka

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.SavedProfileEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class SavedProfilesViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val profiles: StateFlow<List<SavedProfileEntity>> = repository.getSavedProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editingProfile = MutableStateFlow<SavedProfileEntity?>(null)
    val editingProfile: StateFlow<SavedProfileEntity?> = _editingProfile

    fun startEditing(profile: SavedProfileEntity?) {
        _editingProfile.value = profile
    }

    fun clearEditing() {
        _editingProfile.value = null
    }

    fun saveProfile(
        existingId: Long?,
        name: String,
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        city: String,
        latitude: Double,
        longitude: Double,
        timezoneId: String,
        timezoneOffsetHours: Double,
    ) {
        viewModelScope.launch {
            val now = Clock.System.now().toEpochMilliseconds()
            if (existingId != null && existingId > 0) {
                val existing = repository.getSavedProfileById(existingId)
                if (existing != null) {
                    repository.updateProfile(
                        existing.copy(
                            name = name,
                            year = year,
                            month = month,
                            day = day,
                            hour = hour,
                            minute = minute,
                            city = city,
                            latitude = latitude,
                            longitude = longitude,
                            timezoneId = timezoneId,
                            timezoneOffsetHours = timezoneOffsetHours,
                            updatedAt = now,
                        )
                    )
                }
            } else {
                // Check if a profile with same name + birth date/time already exists
                val match = repository.findProfileByNameAndBirth(name, year, month, day, hour, minute)
                if (match != null) {
                    // Update existing profile (location may have changed)
                    repository.updateProfile(
                        match.copy(
                            city = city,
                            latitude = latitude,
                            longitude = longitude,
                            timezoneId = timezoneId,
                            timezoneOffsetHours = timezoneOffsetHours,
                            updatedAt = now,
                        )
                    )
                } else {
                    repository.insertProfile(
                        SavedProfileEntity(
                            name = name,
                            year = year,
                            month = month,
                            day = day,
                            hour = hour,
                            minute = minute,
                            city = city,
                            latitude = latitude,
                            longitude = longitude,
                            timezoneId = timezoneId,
                            timezoneOffsetHours = timezoneOffsetHours,
                            createdAt = now,
                            updatedAt = now,
                        )
                    )
                }
            }
            _editingProfile.value = null
        }
    }

    fun deleteProfile(profile: SavedProfileEntity) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
        }
    }
}
