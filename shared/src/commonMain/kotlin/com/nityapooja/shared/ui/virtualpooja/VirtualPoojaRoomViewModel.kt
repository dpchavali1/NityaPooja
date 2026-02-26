package com.nityapooja.shared.ui.virtualpooja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.DeityEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

// Types are defined in PoojaRoomState.kt

class VirtualPoojaRoomViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VirtualPoojaRoomUiState())
    val uiState: StateFlow<VirtualPoojaRoomUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllDeities().collect { deities ->
                val selectedId = _uiState.value.selectedDeityId ?: deities.firstOrNull()?.id
                _uiState.update { it.copy(allDeities = deities, selectedDeityId = selectedId, isLoading = false) }
                selectedId?.let { loadDeityData(it) }
            }
        }
    }

    fun selectDeity(deityId: Int) {
        if (_uiState.value.selectedDeityId == deityId) return
        _uiState.update {
            it.copy(
                selectedDeityId = deityId,
                offerings = PoojaItem.entries.associateWith { item -> OfferingState(item) },
                floatingPetals = emptyList(),
                smokeParticles = emptyList(),
                showCompletionBanner = false,
            )
        }
        loadDeityData(deityId)
    }

    private fun loadDeityData(deityId: Int) {
        viewModelScope.launch {
            repository.getDeityById(deityId).firstOrNull()?.let { deity ->
                _uiState.update { it.copy(selectedDeity = deity) }
            }
        }
    }

    fun performOffering(item: PoojaItem) {
        // Hide abhishekam toggle whenever any OTHER offering is tapped
        val hideToggle = item != PoojaItem.ABHISHEKAM

        // Ghanta is always re-tappable (no isDone tracking)
        if (item == PoojaItem.GHANTA) {
            _uiState.update { state ->
                val updated = state.offerings.toMutableMap()
                updated[item] = OfferingState(item, isDone = true, isAnimating = true)
                state.copy(offerings = updated, showAbhishekamToggle = false)
            }
            viewModelScope.launch {
                delay(800)
                _uiState.update { state ->
                    val updated = state.offerings.toMutableMap()
                    updated[item] = OfferingState(item, isDone = true, isAnimating = false)
                    state.copy(offerings = updated)
                }
            }
            return
        }

        // Harathi is toggle: tap to start, tap again to stop
        if (item == PoojaItem.HARATHI) {
            _uiState.update { state ->
                val current = state.offerings[item]
                val updated = state.offerings.toMutableMap()
                if (current?.isAnimating == true) {
                    updated[item] = OfferingState(item, isDone = true, isAnimating = false)
                } else {
                    updated[item] = OfferingState(item, isDone = true, isAnimating = true)
                }
                val allDone = updated.values.all { it.isDone }
                state.copy(offerings = updated, showCompletionBanner = allDone, showAbhishekamToggle = false)
            }
            return
        }

        // Abhishekam: show the water/milk toggle
        val showToggle = item == PoojaItem.ABHISHEKAM

        _uiState.update { state ->
            val updated = state.offerings.toMutableMap()
            updated[item] = OfferingState(item, isDone = true, isAnimating = true)

            val newPetals = if (item == PoojaItem.PUSHPAM) {
                state.floatingPetals + generatePetals(state.floatingPetals.size)
            } else state.floatingPetals

            val newSmoke = if (item == PoojaItem.DHOOP) {
                state.smokeParticles + generateSmoke(state.smokeParticles.size)
            } else state.smokeParticles

            state.copy(
                offerings = updated,
                floatingPetals = newPetals,
                smokeParticles = newSmoke,
                showAbhishekamToggle = showToggle,
            )
        }

        // Clear animating flag after burst, check for completion
        viewModelScope.launch {
            delay(1500)
            _uiState.update { state ->
                val updated = state.offerings.toMutableMap()
                updated[item] = OfferingState(item, isDone = true, isAnimating = false)
                val allDone = updated.values.all { it.isDone }
                state.copy(offerings = updated, showCompletionBanner = allDone)
            }
        }
    }

    fun toggleAbhishekamType() {
        _uiState.update {
            it.copy(
                abhishekamType = if (it.abhishekamType == AbhishekamType.WATER)
                    AbhishekamType.MILK else AbhishekamType.WATER,
                showAbhishekamToggle = false, // hide after selection
            )
        }
    }

    fun dismissCompletionBanner() {
        _uiState.update { it.copy(showCompletionBanner = false) }
    }

    fun resetPooja() {
        _uiState.update {
            it.copy(
                offerings = PoojaItem.entries.associateWith { item -> OfferingState(item) },
                floatingPetals = emptyList(),
                smokeParticles = emptyList(),
                showCompletionBanner = false,
            )
        }
    }

    private fun generatePetals(existingCount: Int): List<FloatingPetal> {
        return (0 until 6).map { i ->
            FloatingPetal(
                id = existingCount + i,
                startX = Random.nextFloat() * 0.7f + 0.15f,
                rotation = Random.nextFloat() * 60f - 30f,
                sizeFactor = 0.8f + Random.nextFloat() * 0.5f,
                colorIndex = Random.nextInt(4),
            )
        }
    }

    private fun generateSmoke(existingCount: Int): List<SmokeParticle> {
        return (0 until 4).map { i ->
            SmokeParticle(
                id = existingCount + i,
                startX = 0.4f + Random.nextFloat() * 0.2f,
                driftX = Random.nextFloat() * 0.1f - 0.05f,
            )
        }
    }
}
