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
import kotlinx.coroutines.flow.map
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
    val chandraBalam: MuhurtamRules.ChandraBalam? = null,
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

    // Selected person name (for display)
    private val _selectedPersonName = MutableStateFlow("")
    val selectedPersonName: StateFlow<String> = _selectedPersonName.asStateFlow()

    // Selected rashi for Chandrabalam scoring
    private val _selectedRashi = MutableStateFlow("")
    val selectedRashi: StateFlow<String> = _selectedRashi.asStateFlow()

    // Family profiles: list of name:nakshatra:rashi triplets
    data class FamilyMember(val name: String, val nakshatra: String, val rashi: String = "")

    val familyMembers: StateFlow<List<FamilyMember>> = preferencesManager.familyProfiles
        .map { str: String ->
            if (str.isBlank()) emptyList<FamilyMember>()
            else str.split(",").mapNotNull { entry ->
                val parts = entry.split(":")
                when {
                    parts.size >= 3 -> FamilyMember(parts[0].trim(), parts[1].trim(), parts[2].trim())
                    parts.size == 2 -> FamilyMember(parts[0].trim(), parts[1].trim())
                    else -> null
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFamilyMember(name: String, nakshatra: String, rashi: String = "") {
        viewModelScope.launch {
            val current = familyMembers.value.toMutableList()
            current.add(FamilyMember(name.trim(), nakshatra.trim(), rashi.trim()))
            preferencesManager.setFamilyProfiles(current.joinToString(",") { "${it.name}:${it.nakshatra}:${it.rashi}" })
        }
    }

    fun removeFamilyMember(index: Int) {
        viewModelScope.launch {
            val current = familyMembers.value.toMutableList()
            if (index in current.indices) {
                current.removeAt(index)
                preferencesManager.setFamilyProfiles(current.joinToString(",") { "${it.name}:${it.nakshatra}:${it.rashi}" })
            }
        }
    }

    fun selectFamilyMember(member: FamilyMember) {
        _selectedNakshatra.value = member.nakshatra
        _selectedRashi.value = member.rashi
        _selectedPersonName.value = member.name
        if (cachedPanchangams.isNotEmpty()) {
            scoreWithEvent(_selectedEvent.value)
        }
    }

    fun selectSelf() {
        _selectedNakshatra.value = userNakshatra.value
        _selectedRashi.value = ""
        _selectedPersonName.value = ""
        if (cachedPanchangams.isNotEmpty()) {
            scoreWithEvent(_selectedEvent.value)
        }
    }

    fun selectNakshatraAndRashi(nakshatra: String, rashi: String = "") {
        _selectedNakshatra.value = nakshatra
        _selectedRashi.value = rashi
        _selectedPersonName.value = ""
        if (cachedPanchangams.isNotEmpty()) {
            scoreWithEvent(_selectedEvent.value)
        }
    }

    private fun getSelectedRashiIndex(): Int {
        val name = _selectedRashi.value
        return if (name.isNotBlank()) MuhurtamRules.rashiIndexFromTelugu(name) else -1
    }

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
        _selectedPersonName.value = ""
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
        val rashiIndex = getSelectedRashiIndex()
        _scoredDates.value = cachedPanchangams.map { panchangam ->
            val taraBalam = if (birthIndex >= 0) {
                MuhurtamRules.calculateTaraBalam(birthIndex, panchangam.nakshatra.index)
            } else null
            val chandraBalam = if (rashiIndex >= 0) {
                MuhurtamRules.calculateChandraBalam(rashiIndex, panchangam.moonRashi.index)
            } else null
            ScoredDate(
                panchangamData = panchangam,
                result = MuhurtamRules.scoreMuhurtam(panchangam, eventType, birthIndex, rashiIndex),
                taraBalam = taraBalam,
                chandraBalam = chandraBalam,
            )
        }.sortedByDescending { it.result.points }
    }
}
