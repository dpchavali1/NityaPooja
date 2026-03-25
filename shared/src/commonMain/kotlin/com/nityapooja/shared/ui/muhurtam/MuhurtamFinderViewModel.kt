package com.nityapooja.shared.ui.muhurtam

import androidx.lifecycle.ViewModel
import com.nityapooja.shared.data.muhurtam.MuhurtamRules
import com.nityapooja.shared.data.muhurtam.MuhurtamRules.EventType
import com.nityapooja.shared.data.muhurtam.MuhurtamRules.MuhurtamResult
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.ui.panchangam.PanchangamData
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import com.nityapooja.shared.ui.panchangam.SelectedDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

data class ScoredDate(
    val panchangamData: PanchangamData,
    val result: MuhurtamResult,
)

class MuhurtamFinderViewModel(
    private val preferencesManager: UserPreferencesManager,
) : ViewModel() {

    private val panchangamViewModel = PanchangamViewModel(preferencesManager)

    private val _selectedEvent = MutableStateFlow(EventType.GRIHA_PRAVESHAM)
    val selectedEvent: StateFlow<EventType> = _selectedEvent.asStateFlow()

    private val _scoredDates = MutableStateFlow<List<ScoredDate>>(emptyList())
    val scoredDates: StateFlow<List<ScoredDate>> = _scoredDates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var cachedPanchangams: List<PanchangamData> = emptyList()
    private var cachedLat = 0.0
    private var cachedLng = 0.0
    private var cachedTimezone = ""

    fun selectEvent(eventType: EventType) {
        _selectedEvent.value = eventType
        if (cachedPanchangams.isNotEmpty()) {
            scoreWithEvent(eventType)
        }
    }

    fun calculate(lat: Double, lng: Double, timezone: String) {
        if (lat == cachedLat && lng == cachedLng && timezone == cachedTimezone && cachedPanchangams.isNotEmpty()) {
            scoreWithEvent(_selectedEvent.value)
            return
        }

        _isLoading.value = true
        cachedLat = lat
        cachedLng = lng
        cachedTimezone = timezone

        val today = Clock.System.todayIn(TimeZone.of(timezone))
        val panchangams = mutableListOf<PanchangamData>()

        for (i in 0 until 30) {
            val date = today.plus(i, DateTimeUnit.DAY)
            val selectedDate = SelectedDate(date.year, date.monthNumber, date.dayOfMonth)
            val data = panchangamViewModel.calculatePanchangam(lat, lng, timezone, selectedDate)
            panchangams.add(data)
        }

        cachedPanchangams = panchangams
        _isLoading.value = false
        scoreWithEvent(_selectedEvent.value)
    }

    private fun scoreWithEvent(eventType: EventType) {
        _scoredDates.value = cachedPanchangams.map { panchangam ->
            ScoredDate(
                panchangamData = panchangam,
                result = MuhurtamRules.scoreMuhurtam(panchangam, eventType),
            )
        }.sortedByDescending { it.result.points }
    }
}
