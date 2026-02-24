package com.nityapooja.app.ui.puja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.local.entity.PujaStepEntity
import com.nityapooja.app.data.repository.DevotionalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuidedPujaViewModel @Inject constructor(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val pujaTypes: StateFlow<List<String>> = repository.getAllPujaTypes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _steps = MutableStateFlow<List<PujaStepEntity>>(emptyList())
    val steps: StateFlow<List<PujaStepEntity>> = _steps.asStateFlow()

    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex.asStateFlow()

    private val _selectedTier = MutableStateFlow("standard")
    val selectedTier: StateFlow<String> = _selectedTier.asStateFlow()

    fun loadSteps(pujaType: String, tier: String) {
        _currentStepIndex.value = 0
        _selectedTier.value = tier
        viewModelScope.launch {
            val stepList = repository.getPujaSteps(pujaType, tier).first()
            if (stepList.isEmpty() && tier != "standard") {
                // Fall back to standard tier if requested tier has no data
                val fallbackList = repository.getPujaSteps(pujaType, "standard").first()
                _steps.value = fallbackList
            } else {
                _steps.value = stepList
            }
        }
    }

    fun nextStep() {
        val maxIndex = _steps.value.size - 1
        if (_currentStepIndex.value < maxIndex) {
            _currentStepIndex.value++
        }
    }

    fun previousStep() {
        if (_currentStepIndex.value > 0) {
            _currentStepIndex.value--
        }
    }

    fun setTier(tier: String) {
        _selectedTier.value = tier
    }
}
