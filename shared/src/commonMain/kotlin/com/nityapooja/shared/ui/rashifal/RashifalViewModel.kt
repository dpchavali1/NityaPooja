package com.nityapooja.shared.ui.rashifal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.RashiEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class RashifalViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val rashis: StateFlow<List<RashiEntity>> = repository.getAllRashis()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedRashi = MutableStateFlow<RashiEntity?>(null)
    val selectedRashi: StateFlow<RashiEntity?> = _selectedRashi.asStateFlow()

    fun selectRashi(id: Int) {
        viewModelScope.launch {
            repository.getRashiById(id).collect { rashi ->
                _selectedRashi.value = rashi
            }
        }
    }

    fun clearSelection() {
        _selectedRashi.value = null
    }

    fun getTodayPrediction(rashi: RashiEntity): Pair<String?, String?> {
        val dayOfWeek = Clock.System.todayIn(TimeZone.currentSystemDefault()).dayOfWeek
        return when (dayOfWeek) {
            DayOfWeek.SUNDAY -> Pair(rashi.predictionSunTelugu, rashi.predictionSun)
            DayOfWeek.MONDAY -> Pair(rashi.predictionMonTelugu, rashi.predictionMon)
            DayOfWeek.TUESDAY -> Pair(rashi.predictionTueTelugu, rashi.predictionTue)
            DayOfWeek.WEDNESDAY -> Pair(rashi.predictionWedTelugu, rashi.predictionWed)
            DayOfWeek.THURSDAY -> Pair(rashi.predictionThuTelugu, rashi.predictionThu)
            DayOfWeek.FRIDAY -> Pair(rashi.predictionFriTelugu, rashi.predictionFri)
            DayOfWeek.SATURDAY -> Pair(rashi.predictionSatTelugu, rashi.predictionSat)
            else -> Pair(null, null)
        }
    }
}
