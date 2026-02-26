package com.nityapooja.shared.ui.bhajan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.BhajanEntity
import com.nityapooja.shared.data.local.entity.DeityEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BhajanViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allBhajans: StateFlow<List<BhajanEntity>> = repository.getAllBhajans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deityMap: StateFlow<Map<Int, DeityEntity>> = repository.getAllDeities()
        .map { deities -> deities.associateBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun getBhajanById(id: Int): Flow<BhajanEntity?> = repository.getBhajanById(id)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
