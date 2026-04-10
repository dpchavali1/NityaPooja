package com.nityapooja.shared.ui.japa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.dao.JapaSessionDao
import com.nityapooja.shared.data.local.entity.JapaSessionEntity
import com.nityapooja.shared.data.local.entity.MantraEntity
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
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

    private val _todayDate = MutableStateFlow(Clock.System.todayIn(TimeZone.currentSystemDefault()).toString())

    @OptIn(ExperimentalCoroutinesApi::class)
    val todaySessions: StateFlow<List<JapaSessionEntity>> = _todayDate
        .flatMapLatest { date -> japaSessionDao.getSessionsByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayMalas: StateFlow<Int> = _todayDate
        .flatMapLatest { date -> japaSessionDao.getSessionsByDate(date) }
        .map { sessions -> sessions.sumOf { it.malasCompleted } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val allSessionDates = japaSessionDao.getAllSessionDates()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), replay = 1)

    val currentStreak: StateFlow<Int> = allSessionDates
        .map { dates -> calculateStreak(dates) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val longestStreak: StateFlow<Int> = allSessionDates
        .map { dates -> calculateLongestStreak(dates) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** true/false for each of the last 7 days (index 0 = 6 days ago, index 6 = today) */
    val last7DaysActivity: StateFlow<List<Boolean>> = allSessionDates
        .map { dates ->
            val dateSet = dates.toHashSet()
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            (6 downTo 0).map { daysBack ->
                val d = today.toEpochDays() - daysBack
                // rebuild date string from epoch days via LocalDate
                LocalDate.fromEpochDays(d).toString() in dateSet
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), List(7) { false })

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

    private fun calculateLongestStreak(dates: List<String>): Int {
        if (dates.isEmpty()) return 0
        val sorted = dates.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.sortedDescending()
        if (sorted.isEmpty()) return 0
        var longest = 1
        var current = 1
        for (i in 0 until sorted.size - 1) {
            val diff = sorted[i].toEpochDays() - sorted[i + 1].toEpochDays()
            if (diff == 1) {
                current++
                if (current > longest) longest = current
            } else {
                current = 1
            }
        }
        return longest
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
        // Always compute the current date at save time — never rely on the stale VM-init value.
        val date = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        _todayDate.value = date

        viewModelScope.launch {
            japaSessionDao.insertSession(
                JapaSessionEntity(
                    mantraName = mantra?.title ?: "Om",
                    mantraNameTelugu = mantra?.titleTelugu ?: "ఓం",
                    count = currentCount,
                    malasCompleted = _malas.value,
                    date = date,
                    durationSeconds = duration,
                )
            )
        }

        _count.value = 0
        _malas.value = 0
        _sessionStartTime.value = Clock.System.now().toEpochMilliseconds()
    }
}
