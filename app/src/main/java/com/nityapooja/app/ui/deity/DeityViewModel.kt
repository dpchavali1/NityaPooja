package com.nityapooja.app.ui.deity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.local.entity.*
import com.nityapooja.app.data.repository.DevotionalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class DeityViewModel @Inject constructor(
    private val repository: DevotionalRepository,
) : ViewModel() {

    fun getDeityById(id: Int) = repository.getDeityById(id)

    fun getAartisByDeity(deityId: Int): StateFlow<List<AartiEntity>> =
        repository.getAartisByDeity(deityId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getStotramsByDeity(deityId: Int): StateFlow<List<StotramEntity>> =
        repository.getStotramsByDeity(deityId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getMantrasByDeity(deityId: Int): StateFlow<List<MantraEntity>> =
        repository.getMantrasByDeity(deityId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getKeertanaluByDeity(deityId: Int): StateFlow<List<KeertanaEntity>> =
        repository.getKeertanaluByDeity(deityId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getChalisasByDeity(deityId: Int): StateFlow<List<ChalisaEntity>> =
        repository.getChalisasByDeity(deityId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getSuprabhatamByDeity(deityId: Int): StateFlow<List<SuprabhatamEntity>> =
        repository.getSuprabhatamByDeity(deityId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getAshtotraByDeity(deityId: Int): StateFlow<List<AshtotraEntity>> =
        repository.getAshtotraByDeity(deityId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
