package com.nityapooja.shared.ui.deity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.*
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeityViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    fun getDeityById(id: Int): Flow<DeityEntity?> = repository.getDeityById(id)

    fun getAartisByDeity(deityId: Int): Flow<List<AartiEntity>> =
        repository.getAartisByDeity(deityId)

    fun getStotramsByDeity(deityId: Int): Flow<List<StotramEntity>> =
        repository.getStotramsByDeity(deityId)

    fun getMantrasByDeity(deityId: Int): Flow<List<MantraEntity>> =
        repository.getMantrasByDeity(deityId)

    fun getKeertanaluByDeity(deityId: Int): Flow<List<KeertanaEntity>> =
        repository.getKeertanaluByDeity(deityId)

    fun getChalisasByDeity(deityId: Int): Flow<List<ChalisaEntity>> =
        repository.getChalisasByDeity(deityId)

    fun getSuprabhatamByDeity(deityId: Int): Flow<List<SuprabhatamEntity>> =
        repository.getSuprabhatamByDeity(deityId)

    fun getAshtotraByDeity(deityId: Int): Flow<List<AshtotraEntity>> =
        repository.getAshtotraByDeity(deityId)

    fun trackHistory(contentType: String, contentId: Int, title: String, titleTelugu: String) {
        viewModelScope.launch {
            repository.addToHistory(contentType, contentId, title, titleTelugu)
        }
    }
}
