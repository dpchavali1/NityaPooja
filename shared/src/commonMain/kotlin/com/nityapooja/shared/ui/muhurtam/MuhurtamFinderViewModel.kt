package com.nityapooja.shared.ui.muhurtam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.muhurtam.MuhurtamRules
import kotlinx.coroutines.Dispatchers
import com.nityapooja.shared.data.muhurtam.MuhurtamRules.EventType
import com.nityapooja.shared.data.muhurtam.MuhurtamRules.MuhurtamResult
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.ui.panchangam.PanchangamData
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import com.nityapooja.shared.ui.panchangam.SelectedDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

data class ScoredDate(
    val panchangamData: PanchangamData,
    val result: MuhurtamResult,
    val taraBalam: MuhurtamRules.TaraBalam? = null,
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

    val userNakshatra: StateFlow<String> = preferencesManager.nakshatra
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    // Selected nakshatra for scoring — defaults to user's birth star, can be changed
    private val _selectedNakshatra = MutableStateFlow("")
    val selectedNakshatra: StateFlow<String> = _selectedNakshatra.asStateFlow()

    private var cachedPanchangams: List<PanchangamData> = emptyList()
    private var cachedLat = 0.0
    private var cachedLng = 0.0
    private var cachedTimezone = ""
    private var initialized = false

    fun getSelectedNakshatraIndex(): Int {
        val name = _selectedNakshatra.value
        return if (name.isNotBlank()) MuhurtamRules.nakshatraIndexFromTelugu(name) else -1
    }

    fun selectNakshatra(nakshatra: String) {
        _selectedNakshatra.value = nakshatra
        if (cachedPanchangams.isNotEmpty()) {
            scoreWithEvent(_selectedEvent.value)
        }
    }

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

        cachedLat = lat
        cachedLng = lng
        cachedTimezone = timezone

        // Default selected nakshatra to user's birth star on first load
        if (!initialized) {
            initialized = true
            _selectedNakshatra.value = userNakshatra.value
        }

        // Progressive loading: first 7 days fast, then remaining 23 in background
        _isLoading.value = true
        viewModelScope.launch {
            // Phase 1: First 7 days (shows results quickly)
            val first7 = withContext(Dispatchers.Default) {
                val today = Clock.System.todayIn(TimeZone.of(timezone))
                (0 until 7).map { i ->
                    val date = today.plus(i, DateTimeUnit.DAY)
                    panchangamViewModel.calculatePanchangam(lat, lng, timezone, SelectedDate(date.year, date.monthNumber, date.dayOfMonth))
                }
            }
            cachedPanchangams = first7
            _isLoading.value = false
            scoreWithEvent(_selectedEvent.value)

            // Phase 2: Remaining 23 days in background
            val remaining = withContext(Dispatchers.Default) {
                val today = Clock.System.todayIn(TimeZone.of(timezone))
                (7 until 30).map { i ->
                    val date = today.plus(i, DateTimeUnit.DAY)
                    panchangamViewModel.calculatePanchangam(lat, lng, timezone, SelectedDate(date.year, date.monthNumber, date.dayOfMonth))
                }
            }
            cachedPanchangams = first7 + remaining
            scoreWithEvent(_selectedEvent.value)
        }
    }

    private fun scoreWithEvent(eventType: EventType) {
        val birthIndex = getSelectedNakshatraIndex()
        _scoredDates.value = cachedPanchangams.map { panchangam ->
            val taraBalam = if (birthIndex >= 0) {
                MuhurtamRules.calculateTaraBalam(birthIndex, panchangam.nakshatra.index)
            } else null
            ScoredDate(
                panchangamData = panchangam,
                result = MuhurtamRules.scoreMuhurtam(panchangam, eventType, birthIndex),
                taraBalam = taraBalam,
            )
        }.sortedByDescending { it.result.points }
    }
}
