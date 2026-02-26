package com.nityapooja.shared.ui.suprabhatam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.DeityEntity
import com.nityapooja.shared.data.local.entity.SuprabhatamEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SuprabhatamViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allSuprabhatam: StateFlow<List<SuprabhatamEntity>> = repository.getAllSuprabhatam()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deityMap: StateFlow<Map<Int, DeityEntity>> = repository.getAllDeities()
        .map { deities -> deities.associateBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun getSuprabhatamById(id: Int): Flow<SuprabhatamEntity?> = repository.getSuprabhatamById(id)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
