package com.nityapooja.shared.ui.japa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.dao.JapaSessionDao
import com.nityapooja.shared.data.local.entity.JapaSessionEntity
import com.nityapooja.shared.data.local.entity.MantraEntity
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class JapaViewModel(
    private val repository: DevotionalRepository,
    private val japaSessionDao: JapaSessionDao,
    private val preferencesManager: UserPreferencesManager,
) : ViewModel() {

    val mantras: StateFlow<List<MantraEntity>> = repository.getAllMantras()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedMantra = MutableStateFlow<MantraEntity?>(null)
    val selectedMantra: StateFlow<MantraEntity?> = _selectedMantra.asStateFlow()

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    private val _malas = MutableStateFlow(0)
    val malas: StateFlow<Int> = _malas.asStateFlow()

    val targetMalas: StateFlow<Int> = preferencesManager.japaTargetMalas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3)

    private val _sessionStartTime = MutableStateFlow(Clock.System.now().toEpochMilliseconds())

    val totalMalas: StateFlow<Int> = japaSessionDao.getTotalMalas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCount: StateFlow<Int> = japaSessionDao.getTotalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val todayDate: String = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

    val todaySessions: StateFlow<List<JapaSessionEntity>> = japaSessionDao.getSessionsByDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayMalas: StateFlow<Int> = japaSessionDao.getSessionsByDate(todayDate)
        .map { sessions -> sessions.sumOf { it.malasCompleted } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val currentStreak: StateFlow<Int> = japaSessionDao.getAllSessionDates()
        .map { dates -> calculateStreak(dates) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeDaysCount: StateFlow<Int> = japaSessionDao.getActiveDaysCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun calculateStreak(dates: List<String>): Int {
        if (dates.isEmpty()) return 0

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        val sortedDates = dates.mapNotNull { dateStr ->
            try { LocalDate.parse(dateStr) } catch (_: Exception) { null }
        }.sortedDescending()

        if (sortedDates.isEmpty()) return 0

        // Check if the latest session is today or yesterday (streak is still alive)
        val daysSinceLastSession = today.toEpochDays() - sortedDates.first().toEpochDays()
        if (daysSinceLastSession > 1) return 0

        var streak = 1
        for (i in 0 until sortedDates.size - 1) {
            val diff = sortedDates[i].toEpochDays() - sortedDates[i + 1].toEpochDays()
            if (diff == 1) {
                streak++
            } else {
                break
            }
        }
        return streak
    }

    fun selectMantra(mantra: MantraEntity) {
        _selectedMantra.value = mantra
    }

    fun setTarget(target: Int) {
        viewModelScope.launch { preferencesManager.setJapaTargetMalas(target) }
    }

    fun increment() {
        _count.value++
        if (_count.value % 108 == 0) {
            _malas.value++
        }
    }

    fun saveAndReset() {
        val currentCount = _count.value
        if (currentCount == 0) return

        val duration = (Clock.System.now().toEpochMilliseconds() - _sessionStartTime.value) / 1000
        val mantra = _selectedMantra.value

        viewModelScope.launch {
            japaSessionDao.insertSession(
                JapaSessionEntity(
                    mantraName = mantra?.title ?: "Om",
                    mantraNameTelugu = mantra?.titleTelugu ?: "ఓం",
                    count = currentCount,
                    malasCompleted = _malas.value,
                    date = todayDate,
                    durationSeconds = duration,
                )
            )
        }

        _count.value = 0
        _malas.value = 0
        _sessionStartTime.value = Clock.System.now().toEpochMilliseconds()
    }
}
