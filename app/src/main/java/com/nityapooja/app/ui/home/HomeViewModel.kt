package com.nityapooja.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.local.entity.BookmarkEntity
import com.nityapooja.app.data.local.entity.DeityEntity
import com.nityapooja.app.data.local.entity.FestivalEntity
import com.nityapooja.app.data.local.entity.ReadingHistoryEntity
import com.nityapooja.app.data.local.entity.ShlokaEntity
import com.nityapooja.app.data.preferences.UserPreferencesManager
import com.nityapooja.app.data.repository.DevotionalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DevotionalRepository,
    private val preferencesManager: UserPreferencesManager,
) : ViewModel() {

    val deities: StateFlow<List<DeityEntity>> = repository.getAllDeities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayShloka: StateFlow<ShlokaEntity?> = repository.getShlokaForDay(
        Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
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
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in 4..11 -> "శుభోదయం"
            hour in 12..16 -> "శుభ మధ్యాహ్నం"
            hour in 17..20 -> "శుభ సాయంత్రం"
            else -> "శుభ రాత్రి"
        }
    }

    fun getGreetingEnglish(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in 4..11 -> "Good Morning"
            hour in 12..16 -> "Good Afternoon"
            hour in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }

    fun getTodayDayName(): String {
        return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Monday"
        }
    }

    fun getTodayTeluguDay(): String {
        return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "ఆదివారం"
            Calendar.MONDAY -> "సోమవారం"
            Calendar.TUESDAY -> "మంగళవారం"
            Calendar.WEDNESDAY -> "బుధవారం"
            Calendar.THURSDAY -> "గురువారం"
            Calendar.FRIDAY -> "శుక్రవారం"
            Calendar.SATURDAY -> "శనివారం"
            else -> "సోమవారం"
        }
    }
}
