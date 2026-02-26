package com.nityapooja.shared.ui.temple

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.TempleEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TempleViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allTemples: StateFlow<List<TempleEntity>> = repository.getAllTemples()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveDarshanTemples: StateFlow<List<TempleEntity>> = repository.getLiveDarshanTemples()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTempleById(id: Int): Flow<TempleEntity?> = repository.getTempleById(id)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
