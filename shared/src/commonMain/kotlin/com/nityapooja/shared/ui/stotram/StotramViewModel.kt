package com.nityapooja.shared.ui.stotram

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.DeityEntity
import com.nityapooja.shared.data.local.entity.StotramEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StotramViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allStotrams: StateFlow<List<StotramEntity>> = repository.getAllStotrams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deityMap: StateFlow<Map<Int, DeityEntity>> = repository.getAllDeities()
        .map { deities -> deities.associateBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun getStotramById(id: Int): Flow<StotramEntity?> = repository.getStotramById(id)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
