package com.nityapooja.shared.ui.keertana

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.DeityEntity
import com.nityapooja.shared.data.local.entity.KeertanaEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class KeertanaViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allKeertanalu: StateFlow<List<KeertanaEntity>> = repository.getAllKeertanalu()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deityMap: StateFlow<Map<Int, DeityEntity>> = repository.getAllDeities()
        .map { deities -> deities.associateBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val composers: StateFlow<List<String>> = repository.getAllComposers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedComposer = MutableStateFlow<String?>(null)
    val selectedComposer: StateFlow<String?> = _selectedComposer.asStateFlow()

    val filteredKeertanalu: StateFlow<List<KeertanaEntity>> =
        combine(allKeertanalu, _selectedComposer) { keertanalu, composer ->
            if (composer == null) keertanalu
            else keertanalu.filter { it.composer == composer }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectComposer(composer: String?) {
        _selectedComposer.value = composer
    }

    fun getKeertanaById(id: Int): Flow<KeertanaEntity?> = repository.getKeertanaById(id)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
