package com.nityapooja.shared.ui.family

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.dao.FamilyDayDao
import com.nityapooja.shared.data.local.entity.FamilyDayEntity
import com.nityapooja.shared.data.local.entity.FamilyDayType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class UpcomingFamilyDay(
    val entity: FamilyDayEntity,
    val daysUntil: Int,
    val nextDateDisplay: String,
)

/** A single year's computed Gregorian date for a tithi anniversary. */
data class TithiFutureDate(
    val year: Int,
    val shortDate: String,  // e.g. "Nov 18"
    val fullDate: String,   // e.g. "Nov 18, 2026"
)

/**
 * Rich tithi details for display on the family day card.
 *
 * @param tithiNameTel  Full tithi label with paksha, e.g. "కృష్ణ పక్ష నవమి"
 * @param refDateDisplay Formatted original date user entered, e.g. "Nov 15, 2021"
 * @param futureDates   Next 3 computed Gregorian occurrences of this tithi
 */
data class TithiInfo(
    val tithiNameTel: String,
    val refDateDisplay: String,
    val futureDates: List<TithiFutureDate>,
)

class FamilyDayViewModel(
    private val familyDayDao: FamilyDayDao,
) : ViewModel() {

    val allDays: StateFlow<List<FamilyDayEntity>> = familyDayDao.getAllFamilyDays()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _upcomingDays = MutableStateFlow<List<UpcomingFamilyDay>>(emptyList())
    val upcomingDays: StateFlow<List<UpcomingFamilyDay>> = _upcomingDays.asStateFlow()

    // entity id → rich tithi details for card display
    private val _tithiDetails = MutableStateFlow<Map<Long, TithiInfo>>(emptyMap())
    val tithiDetails: StateFlow<Map<Long, TithiInfo>> = _tithiDetails.asStateFlow()

    init {
        viewModelScope.launch {
            allDays.collect { days ->
                recomputeUpcoming(days)
            }
        }
    }

    fun addDay(entity: FamilyDayEntity) {
        viewModelScope.launch {
            familyDayDao.insertFamilyDay(entity)
        }
    }

    fun updateDay(entity: FamilyDayEntity) {
        viewModelScope.launch {
            familyDayDao.updateFamilyDay(entity)
        }
    }

    fun deleteDay(id: Long) {
        viewModelScope.launch {
            familyDayDao.deleteFamilyDayById(id)
        }
    }

    private fun recomputeUpcoming(days: List<FamilyDayEntity>) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val todayYear = today.year
        val todayMonth = today.monthNumber
        val todayDay = today.dayOfMonth
        val todayEpoch = today.toEpochDays()

        val upcoming = mutableListOf<UpcomingFamilyDay>()
        val tithiMap = mutableMapOf<Long, TithiInfo>()

        for (entity in days) {
            when (entity.getFamilyDayType()) {
                FamilyDayType.BIRTHDAY, FamilyDayType.ANNIVERSARY, FamilyDayType.CUSTOM -> {
                    val month = entity.gregorianMonth.takeIf { it in 1..12 } ?: continue
                    val day = entity.gregorianDay.takeIf { it in 1..31 } ?: continue

                    // Find next occurrence: this year or next year
                    val thisYearEpoch = epochDaysForMonthDay(todayYear, month, day)
                    val targetEpoch = if (thisYearEpoch >= todayEpoch) thisYearEpoch
                    else epochDaysForMonthDay(todayYear + 1, month, day)

                    val daysUntil = (targetEpoch - todayEpoch).toInt()
                    if (daysUntil > 90) continue

                    val targetYear = if (thisYearEpoch >= todayEpoch) todayYear else todayYear + 1
                    val display = formatDate(targetYear, month, day)
                    upcoming.add(UpcomingFamilyDay(entity, daysUntil, display))
                }

                FamilyDayType.TITHI -> {
                    val refYear = entity.tithiRefYear.takeIf { it > 0 } ?: continue
                    val refMonth = entity.tithiRefMonth.takeIf { it in 1..12 } ?: continue
                    val refDay = entity.tithiRefDay.takeIf { it in 1..31 } ?: continue

                    val tithiIndex = TithiUtils.getTithiIndex(refYear, refMonth, refDay)

                    // Find occurrence this year; if already passed, search next year
                    val thisYearResult = TithiUtils.findTithiDateInYear(tithiIndex, refMonth, refDay, todayYear)
                    val nextResult = if (thisYearResult != null) {
                        val epoch = epochDaysForMonthDay(thisYearResult.first, thisYearResult.second, thisYearResult.third)
                        if (epoch >= todayEpoch) thisYearResult
                        else TithiUtils.findTithiDateInYear(tithiIndex, refMonth, refDay, todayYear + 1)
                    } else {
                        TithiUtils.findTithiDateInYear(tithiIndex, refMonth, refDay, todayYear + 1)
                    }

                    if (nextResult != null) {
                        val epoch = epochDaysForMonthDay(nextResult.first, nextResult.second, nextResult.third)
                        val daysUntil = (epoch - todayEpoch).toInt()
                        if (daysUntil <= 90) {
                            val display = formatDate(nextResult.first, nextResult.second, nextResult.third)
                            upcoming.add(UpcomingFamilyDay(entity, daysUntil, display))
                        }
                    }

                    // Compute next 3 years of tithi dates for this entity
                    val futureDates = mutableListOf<TithiFutureDate>()
                    val startYear = if (thisYearResult != null) {
                        val epoch = epochDaysForMonthDay(thisYearResult.first, thisYearResult.second, thisYearResult.third)
                        if (epoch >= todayEpoch) todayYear else todayYear + 1
                    } else todayYear + 1

                    for (yr in startYear until startYear + 3) {
                        val result = TithiUtils.findTithiDateInYear(tithiIndex, refMonth, refDay, yr)
                        if (result != null) {
                            futureDates.add(
                                TithiFutureDate(
                                    year = result.first,
                                    shortDate = formatShortDate(result.second, result.third),
                                    fullDate = formatDate(result.first, result.second, result.third),
                                )
                            )
                        }
                    }
                    tithiMap[entity.id] = TithiInfo(
                        tithiNameTel = TithiUtils.fullTithiName(tithiIndex),
                        refDateDisplay = formatDate(refYear, refMonth, refDay),
                        futureDates = futureDates,
                    )
                }
            }
        }

        _upcomingDays.value = upcoming.sortedBy { it.daysUntil }
        _tithiDetails.value = tithiMap
    }

    private fun epochDaysForMonthDay(year: Int, month: Int, day: Int): Int {
        // Use kotlinx.datetime LocalDate.toEpochDays()
        return try {
            kotlinx.datetime.LocalDate(year, month, day).toEpochDays()
        } catch (e: Exception) {
            Int.MAX_VALUE
        }
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val monthName = Month(month).name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        return "$monthName ${day.toString().padStart(2, '0')}, $year"
    }

    private fun formatShortDate(month: Int, day: Int): String {
        val monthName = Month(month).name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        return "$monthName ${day.toString().padStart(2, '0')}"
    }
}
