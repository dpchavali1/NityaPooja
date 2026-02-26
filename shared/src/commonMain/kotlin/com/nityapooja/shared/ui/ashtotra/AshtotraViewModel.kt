package com.nityapooja.shared.ui.ashtotra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.AshtotraEntity
import com.nityapooja.shared.data.local.entity.DeityEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AshtotraViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allAshtotra: StateFlow<List<AshtotraEntity>> = repository.getAllAshtotra()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deityMap: StateFlow<Map<Int, DeityEntity>> = repository.getAllDeities()
        .map { deities -> deities.associateBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun getAshtotraById(id: Int): Flow<AshtotraEntity?> = repository.getAshtotraById(id)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
