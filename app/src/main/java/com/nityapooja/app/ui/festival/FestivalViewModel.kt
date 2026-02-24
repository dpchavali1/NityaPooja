package com.nityapooja.app.ui.festival

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.local.entity.FestivalEntity
import com.nityapooja.app.data.repository.DevotionalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FestivalViewModel @Inject constructor(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allFestivals: StateFlow<List<FestivalEntity>> = repository.getAllFestivals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
