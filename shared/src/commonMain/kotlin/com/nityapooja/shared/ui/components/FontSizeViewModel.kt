package com.nityapooja.shared.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FontSizeViewModel(
    private val preferencesManager: UserPreferencesManager,
) : ViewModel() {

    val fontSize: StateFlow<Int> = preferencesManager.fontSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 16)

    fun increaseFontSize() {
        viewModelScope.launch {
            val current = fontSize.value
            if (current < 32) {
                preferencesManager.setFontSize(current + 2)
            }
        }
    }

    fun decreaseFontSize() {
        viewModelScope.launch {
            val current = fontSize.value
            if (current > 10) {
                preferencesManager.setFontSize(current - 2)
            }
        }
    }

    fun decrease() = decreaseFontSize()
    fun increase() = increaseFontSize()
}
