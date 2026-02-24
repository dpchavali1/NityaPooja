package com.nityapooja.app.ui.ashtotra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.local.entity.AshtotraEntity
import com.nityapooja.app.data.repository.DevotionalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AshtotraViewModel @Inject constructor(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allAshtotra: StateFlow<List<AshtotraEntity>> = repository.getAllAshtotra()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getAshtotraById(id: Int): StateFlow<AshtotraEntity?> =
        repository.getAshtotraById(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
