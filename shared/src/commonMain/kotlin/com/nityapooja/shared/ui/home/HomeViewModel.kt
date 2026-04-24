package com.nityapooja.shared.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.BookmarkEntity
import com.nityapooja.shared.data.local.entity.DeityEntity
import com.nityapooja.shared.data.local.entity.FestivalEntity
import com.nityapooja.shared.data.local.entity.ReadingHistoryEntity
import com.nityapooja.shared.data.local.entity.ShlokaEntity
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    private val repository: DevotionalRepository,
    private val preferencesManager: UserPreferencesManager,
) : ViewModel() {

    val deities: StateFlow<List<DeityEntity>> = repository.getAllDeities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Incremented once per calendar day so _today re-evaluates at midnight even when
    // the saved timezone hasn't changed (e.g. long-running session spanning midnight).
    private val _dayTick = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            while (true) {
                val now = Clock.System.now()
                val tzId = preferencesManager.locationTimezone.first()
                val tz = try { TimeZone.of(tzId) } catch (_: Exception) { TimeZone.currentSystemDefault() }
                val midnightInstant = Clock.System.todayIn(tz).plus(DatePeriod(days = 1)).atStartOfDayIn(tz)
                delay((midnightInstant - now).inWholeMilliseconds.coerceAtLeast(60_000L))
                _dayTick.value++
            }
        }
    }

    private val _today: StateFlow<LocalDate> = combine(
        preferencesManager.locationTimezone,
        _dayTick,
    ) { tzId, _ ->
        val tz = try { TimeZone.of(tzId) } catch (_: Exception) { TimeZone.currentSystemDefault() }
        Clock.System.todayIn(tz)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Clock.System.todayIn(TimeZone.currentSystemDefault()))

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayShloka: StateFlow<ShlokaEntity?> = _today
        .flatMapLatest { date -> repository.getShlokaForDay(date.dayOfYear) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val deityOfTheDay: StateFlow<List<DeityEntity>> = _today
        .flatMapLatest { date -> repository.getDeityByDay(dayName(date)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userName: StateFlow<String> = preferencesManager.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val userGotra: StateFlow<String> = preferencesManager.gotra
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val userNakshatra: StateFlow<String> = preferencesManager.nakshatra
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val recentHistory: StateFlow<List<ReadingHistoryEntity>> = repository.getRecentHistory(5)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val morningNotification: StateFlow<Boolean> = preferencesManager.morningNotification
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    data class UpcomingFestival(
        val festival: FestivalEntity,
        val daysUntil: Int,
        val displayDate: String,
    )

    // Single shared subscription — avoids two separate DB queries for the same data
    private val allFestivals = repository.getAllFestivals()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), replay = 1)

    val upcomingFestivals: StateFlow<List<UpcomingFestival>> = allFestivals
        .map { festivals ->
            festivals.mapNotNull { festival ->
                val dateInfo = festival.getUpcomingDateInfo()
                if (dateInfo != null && dateInfo.daysUntil >= 0) {
                    UpcomingFestival(festival, dateInfo.daysUntil, dateInfo.displayDate)
                } else null
            }
            .sortedBy { it.daysUntil }
            .take(3)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Festival that falls on today's date — null if no festival today */
    val todayFestival: StateFlow<FestivalEntity?> = combine(allFestivals, _today) { festivals, today ->
        val todayStr = today.toString()
        festivals.firstOrNull { it.dateThisYear == todayStr || it.dateNextYear == todayStr }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun dayName(date: LocalDate): String {
        return when (date.dayOfWeek) {
            DayOfWeek.SUNDAY -> "Sunday"
            DayOfWeek.MONDAY -> "Monday"
            DayOfWeek.TUESDAY -> "Tuesday"
            DayOfWeek.WEDNESDAY -> "Wednesday"
            DayOfWeek.THURSDAY -> "Thursday"
            DayOfWeek.FRIDAY -> "Friday"
            DayOfWeek.SATURDAY -> "Saturday"
            else -> "Monday"
        }
    }

    val greetingTelugu: StateFlow<String> = _today
        .map { computeGreetingTelugu() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), computeGreetingTelugu())

    val greetingEnglish: StateFlow<String> = _today
        .map { computeGreetingEnglish() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), computeGreetingEnglish())

    private fun computeGreetingTelugu(): String {
        val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
        return when {
            hour in 4..11 -> "శుభోదయం"
            hour in 12..16 -> "శుభ మధ్యాహ్నం"
            hour in 17..20 -> "శుభ సాయంత్రం"
            else -> "శుభ రాత్రి"
        }
    }

    private fun computeGreetingEnglish(): String {
        val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
        return when {
            hour in 4..11 -> "Good Morning"
            hour in 12..16 -> "Good Afternoon"
            hour in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }

    fun getTodayDayName(): String = dayName(Clock.System.todayIn(TimeZone.currentSystemDefault()))

    fun getTodayTeluguDay(): String {
        return when (Clock.System.todayIn(TimeZone.currentSystemDefault()).dayOfWeek) {
            DayOfWeek.SUNDAY -> "ఆదివారం"
            DayOfWeek.MONDAY -> "సోమవారం"
            DayOfWeek.TUESDAY -> "మంగళవారం"
            DayOfWeek.WEDNESDAY -> "బుధవారం"
            DayOfWeek.THURSDAY -> "గురువారం"
            DayOfWeek.FRIDAY -> "శుక్రవారం"
            DayOfWeek.SATURDAY -> "శనివారం"
            else -> "సోమవారం"
        }
    }
}
