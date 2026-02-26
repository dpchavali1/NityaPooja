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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    private val repository: DevotionalRepository,
    private val preferencesManager: UserPreferencesManager,
) : ViewModel() {

    val deities: StateFlow<List<DeityEntity>> = repository.getAllDeities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayShloka: StateFlow<ShlokaEntity?> = repository.getShlokaForDay(
        Clock.System.todayIn(TimeZone.currentSystemDefault()).dayOfYear
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val deityOfTheDay: StateFlow<List<DeityEntity>> = repository.getDeityByDay(
        getTodayDayName()
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    data class UpcomingFestival(
        val festival: FestivalEntity,
        val daysUntil: Int,
        val displayDate: String,
    )

    val upcomingFestivals: StateFlow<List<UpcomingFestival>> = repository.getAllFestivals()
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

    fun getGreetingTelugu(): String {
        val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
        return when {
            hour in 4..11 -> "శుభోదయం"
            hour in 12..16 -> "శుభ మధ్యాహ్నం"
            hour in 17..20 -> "శుభ సాయంత్రం"
            else -> "శుభ రాత్రి"
        }
    }

    fun getGreetingEnglish(): String {
        val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
        return when {
            hour in 4..11 -> "Good Morning"
            hour in 12..16 -> "Good Afternoon"
            hour in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }

    fun getTodayDayName(): String {
        return when (Clock.System.todayIn(TimeZone.currentSystemDefault()).dayOfWeek) {
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
