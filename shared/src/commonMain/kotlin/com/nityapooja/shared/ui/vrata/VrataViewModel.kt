package com.nityapooja.shared.ui.vrata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.dao.VrataDao
import com.nityapooja.shared.data.local.entity.VrataEntity
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.ui.panchangam.PanchangamData
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import com.nityapooja.shared.ui.panchangam.SelectedDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

data class UpcomingVrata(
    val vrata: VrataEntity,
    val dateDisplay: String,
    val teluguDay: String,
    val daysUntil: Int,
    val tithiNameTelugu: String,
    val isFavorite: Boolean = false,
)

class VrataViewModel(
    private val vrataDao: VrataDao,
    private val preferencesManager: UserPreferencesManager,
) : ViewModel() {

    val allVratas: StateFlow<List<VrataEntity>> = vrataDao.getAllVratas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _upcomingVratas = MutableStateFlow<List<UpcomingVrata>>(emptyList())
    val upcomingVratas: StateFlow<List<UpcomingVrata>> = _upcomingVratas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val favoriteIds: StateFlow<Set<Int>> = preferencesManager.favoriteVrataIds
        .map { str -> if (str.isBlank()) emptySet() else str.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val panchangamViewModel = PanchangamViewModel(preferencesManager)

    fun toggleFavorite(vrataId: Int) {
        viewModelScope.launch {
            val current = favoriteIds.value.toMutableSet()
            if (vrataId in current) current.remove(vrataId) else current.add(vrataId)
            preferencesManager.setFavoriteVrataIds(current.joinToString(","))
            // Re-sort upcoming with new favorites
            resortUpcoming()
        }
    }

    fun isFavorite(vrataId: Int): Boolean = vrataId in favoriteIds.value

    fun calculateUpcoming(lat: Double, lng: Double, timezone: String) {
        _isLoading.value = true
        val vratas = allVratas.value

        viewModelScope.launch {
            val upcoming = withContext(Dispatchers.Default) {
                val today = Clock.System.todayIn(TimeZone.of(timezone))
                val result = mutableListOf<UpcomingVrata>()
                for (i in 0 until 30) {
                    val date = today.plus(i, DateTimeUnit.DAY)
                    val selectedDate = SelectedDate(date.year, date.monthNumber, date.dayOfMonth)
                    val panchangam = panchangamViewModel.calculatePanchangam(lat, lng, timezone, selectedDate)
                    val matched = matchVratas(vratas, panchangam)
                    matched.forEach { vrata ->
                        result.add(UpcomingVrata(
                            vrata = vrata,
                            dateDisplay = panchangam.dateDisplay,
                            teluguDay = panchangam.teluguDay,
                            daysUntil = i,
                            tithiNameTelugu = panchangam.tithi.nameTelugu,
                            isFavorite = vrata.id in favoriteIds.value,
                        ))
                    }
                }
                result
            }
            _upcomingVratas.value = sortWithFavorites(upcoming)
            _isLoading.value = false
        }
    }

    private fun resortUpcoming() {
        val current = _upcomingVratas.value
        if (current.isNotEmpty()) {
            _upcomingVratas.value = sortWithFavorites(
                current.map { it.copy(isFavorite = it.vrata.id in favoriteIds.value) }
            )
        }
    }

    private fun sortWithFavorites(list: List<UpcomingVrata>): List<UpcomingVrata> {
        // Favorites first (sorted by daysUntil), then non-favorites (sorted by daysUntil)
        val favs = list.filter { it.isFavorite }.sortedBy { it.daysUntil }
        val others = list.filter { !it.isFavorite }.sortedBy { it.daysUntil }
        return favs + others
    }

    private fun matchVratas(vratas: List<VrataEntity>, panchangam: PanchangamData): List<VrataEntity> {
        val dayOfWeek = dayOfWeekFromTelugu(panchangam.teluguDay)
        val tithiIndex = panchangam.tithi.index
        val isShukla = tithiIndex < 15

        return vratas.filter { vrata ->
            when (vrata.category) {
                "tithi_based" -> {
                    val tithiMatch = vrata.tithiTrigger >= 0 && (tithiIndex % 15) == vrata.tithiTrigger
                    val pakshamMatch = when (vrata.paksham) {
                        "shukla" -> isShukla
                        "krishna" -> !isShukla
                        else -> true
                    }
                    tithiMatch && pakshamMatch
                }
                "vaaram_based" -> {
                    vrata.vaaramTrigger >= 0 && dayOfWeek == vrata.vaaramTrigger
                }
                else -> false
            }
        }
    }

    private fun dayOfWeekFromTelugu(teluguDay: String): Int {
        return when {
            teluguDay.contains("ఆదివారం") -> 1
            teluguDay.contains("సోమవారం") -> 2
            teluguDay.contains("మంగళవారం") -> 3
            teluguDay.contains("బుధవారం") -> 4
            teluguDay.contains("గురువారం") -> 5
            teluguDay.contains("శుక్రవారం") -> 6
            teluguDay.contains("శనివారం") -> 7
            else -> 0
        }
    }
}
