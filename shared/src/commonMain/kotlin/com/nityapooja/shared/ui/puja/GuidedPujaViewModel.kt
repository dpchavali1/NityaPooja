package com.nityapooja.shared.ui.puja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.PujaStepEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GuidedPujaViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allPujaTypes: StateFlow<List<String>> = repository.getAllPujaTypes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _steps = MutableStateFlow<List<PujaStepEntity>>(emptyList())
    val steps: StateFlow<List<PujaStepEntity>> = _steps.asStateFlow()

    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex.asStateFlow()

    private val _selectedTier = MutableStateFlow("basic")
    val selectedTier: StateFlow<String> = _selectedTier.asStateFlow()

    fun loadSteps(pujaType: String, tier: String) {
        viewModelScope.launch {
            repository.getPujaSteps(pujaType, tier).collect { stepList ->
                if (stepList.isNotEmpty()) {
                    _steps.value = stepList
                    _currentStepIndex.value = 0
                } else {
                    // Fallback to basic tier if requested tier has no steps
                    repository.getPujaSteps(pujaType, "basic").collect { fallbackList ->
                        _steps.value = fallbackList
                        _currentStepIndex.value = 0
                    }
                }
            }
        }
    }

    fun nextStep() {
        if (_currentStepIndex.value < _steps.value.size - 1) {
            _currentStepIndex.value += 1
        }
    }

    fun previousStep() {
        if (_currentStepIndex.value > 0) {
            _currentStepIndex.value -= 1
        }
    }

    fun setTier(tier: String) {
        _selectedTier.value = tier
    }
}
