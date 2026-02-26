package com.nityapooja.shared.ui.festival

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.FestivalEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.*

class FestivalViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allFestivals: StateFlow<List<FestivalEntity>> = repository.getAllFestivals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
