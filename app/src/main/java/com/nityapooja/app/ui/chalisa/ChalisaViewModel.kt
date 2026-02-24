package com.nityapooja.app.ui.chalisa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.local.entity.ChalisaEntity
import com.nityapooja.app.data.repository.DevotionalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChalisaViewModel @Inject constructor(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val chalisas: StateFlow<List<ChalisaEntity>> = repository.getAllChalisas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedChalisa = MutableStateFlow<ChalisaEntity?>(null)
    val selectedChalisa: StateFlow<ChalisaEntity?> = _selectedChalisa.asStateFlow()

    fun selectChalisa(id: Int) {
        viewModelScope.launch {
            repository.getChalisaById(id).collect { chalisa ->
                _selectedChalisa.value = chalisa
            }
        }
    }

    fun getChalisaById(id: Int) = repository.getChalisaById(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
