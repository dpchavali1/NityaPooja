package com.nityapooja.app.ui.suprabhatam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.local.entity.SuprabhatamEntity
import com.nityapooja.app.data.repository.DevotionalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuprabhatamViewModel @Inject constructor(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allSuprabhatam: StateFlow<List<SuprabhatamEntity>> = repository.getAllSuprabhatam()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getSuprabhatamById(id: Int): StateFlow<SuprabhatamEntity?> =
        repository.getSuprabhatamById(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
