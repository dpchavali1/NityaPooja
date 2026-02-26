package com.nityapooja.shared.ui.aarti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.AartiEntity
import com.nityapooja.shared.data.local.entity.DeityEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AartiViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allAartis: StateFlow<List<AartiEntity>> = repository.getAllAartis()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deityMap: StateFlow<Map<Int, DeityEntity>> = repository.getAllDeities()
        .map { deities -> deities.associateBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun getAartiById(id: Int): Flow<AartiEntity?> = repository.getAartiById(id)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
