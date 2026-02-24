package com.nityapooja.app.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FontSizeViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager,
) : ViewModel() {

    val fontSize: StateFlow<Int> = preferencesManager.fontSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 16)

    fun increase() {
        viewModelScope.launch {
            preferencesManager.setFontSize(fontSize.value + 2)
        }
    }

    fun decrease() {
        viewModelScope.launch {
            preferencesManager.setFontSize(fontSize.value - 2)
        }
    }
}
