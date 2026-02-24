package com.nityapooja.app.ui.temple

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.local.entity.TempleEntity
import com.nityapooja.app.data.repository.DevotionalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TempleViewModel @Inject constructor(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allTemples: StateFlow<List<TempleEntity>> = repository.getAllTemples()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTempleById(id: Int): StateFlow<TempleEntity?> =
        repository.getTempleById(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val liveDarshanTemples: StateFlow<List<TempleEntity>> = repository.getLiveDarshanTemples()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
