package com.nityapooja.shared.ui.chalisa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.ChalisaEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChalisaViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val chalisas: StateFlow<List<ChalisaEntity>> = repository.getAllChalisas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedChalisa = MutableStateFlow<ChalisaEntity?>(null)
    val selectedChalisa: StateFlow<ChalisaEntity?> = _selectedChalisa.asStateFlow()

    fun selectChalisa(id: Int) {
        viewModelScope.launch {
            repository.getChalisaById(id).collect { _selectedChalisa.value = it }
        }
    }

    fun getChalisaById(id: Int): StateFlow<ChalisaEntity?> =
        repository.getChalisaById(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
