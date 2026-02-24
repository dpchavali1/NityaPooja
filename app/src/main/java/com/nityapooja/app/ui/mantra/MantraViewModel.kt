package com.nityapooja.app.ui.mantra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.local.entity.MantraEntity
import com.nityapooja.app.data.repository.DevotionalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MantraViewModel @Inject constructor(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allMantras: StateFlow<List<MantraEntity>> = repository.getAllMantras()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getMantraById(id: Int): StateFlow<MantraEntity?> =
        repository.getMantraById(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
