package com.nityapooja.app.ui.rashifal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.local.entity.RashiEntity
import com.nityapooja.app.data.repository.DevotionalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class RashifalViewModel @Inject constructor(
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
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.SUNDAY -> Pair(rashi.predictionSunTelugu, rashi.predictionSun)
            Calendar.MONDAY -> Pair(rashi.predictionMonTelugu, rashi.predictionMon)
            Calendar.TUESDAY -> Pair(rashi.predictionTueTelugu, rashi.predictionTue)
            Calendar.WEDNESDAY -> Pair(rashi.predictionWedTelugu, rashi.predictionWed)
            Calendar.THURSDAY -> Pair(rashi.predictionThuTelugu, rashi.predictionThu)
            Calendar.FRIDAY -> Pair(rashi.predictionFriTelugu, rashi.predictionFri)
            Calendar.SATURDAY -> Pair(rashi.predictionSatTelugu, rashi.predictionSat)
            else -> Pair(null, null)
        }
    }
}
