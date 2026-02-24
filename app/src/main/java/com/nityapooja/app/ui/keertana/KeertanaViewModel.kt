package com.nityapooja.app.ui.keertana

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.local.entity.KeertanaEntity
import com.nityapooja.app.data.repository.DevotionalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeertanaViewModel @Inject constructor(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allKeertanalu: StateFlow<List<KeertanaEntity>> = repository.getAllKeertanalu()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val composers: StateFlow<List<String>> = repository.getAllComposers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedComposer = MutableStateFlow<String?>(null)
    val selectedComposer: StateFlow<String?> = _selectedComposer.asStateFlow()

    val filteredKeertanalu: StateFlow<List<KeertanaEntity>> = combine(
        allKeertanalu,
        _selectedComposer,
    ) { keertanalu, composer ->
        if (composer == null) keertanalu
        else keertanalu.filter { it.composer == composer }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectComposer(composer: String?) {
        _selectedComposer.value = if (_selectedComposer.value == composer) null else composer
    }

    fun getKeertanaById(id: Int): StateFlow<KeertanaEntity?> =
        repository.getKeertanaById(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
