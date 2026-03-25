package com.nityapooja.shared.ui.sacredmonth

import androidx.lifecycle.ViewModel
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.data.sacredmonth.SacredMonthData
import com.nityapooja.shared.data.sacredmonth.SacredMonthDateRange
import com.nityapooja.shared.data.sacredmonth.SacredMonthInfo
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

class SacredMonthViewModel(
    private val preferencesManager: UserPreferencesManager,
) : ViewModel() {

    private val panchangamViewModel = PanchangamViewModel(preferencesManager)

    private val _currentSacredMonth = MutableStateFlow<SacredMonthInfo?>(null)
    val currentSacredMonth: StateFlow<SacredMonthInfo?> = _currentSacredMonth.asStateFlow()

    private val _sacredMonthRanges = MutableStateFlow<List<SacredMonthDateRange>>(emptyList())
    val sacredMonthRanges: StateFlow<List<SacredMonthDateRange>> = _sacredMonthRanges.asStateFlow()

    private val _allSacredMonths = MutableStateFlow(SacredMonthData.getAllSacredMonths())
    val allSacredMonths: StateFlow<List<SacredMonthInfo>> = _allSacredMonths.asStateFlow()

    fun detectCurrentMonth(lat: Double, lng: Double, timezone: String) {
        val panchangam = panchangamViewModel.calculatePanchangam(lat, lng, timezone)
        _currentSacredMonth.value = SacredMonthData.getCurrentSacredMonth(panchangam.masa.nameEnglish)

        // Scan next 400 days to find date ranges for all sacred months
        computeDateRanges(lat, lng, timezone)
    }

    private fun computeDateRanges(lat: Double, lng: Double, timezone: String) {
        val today = Clock.System.todayIn(TimeZone.of(timezone))
        val sacredNames = SacredMonthData.getAllSacredMonths().map { it.masaNameEnglish }.toSet()

        // Track: masaName -> (firstDate, lastDate)
        data class MasaSpan(val firstDay: Int, val lastDay: Int) // day offsets from today

        val spans = mutableMapOf<String, MutableList<Int>>() // masaName -> list of day offsets

        // Scan 400 days sampling every day
        for (i in 0 until 400) {
            val date = today.plus(i, DateTimeUnit.DAY)
            val sd = SelectedDate(date.year, date.monthNumber, date.dayOfMonth)
            val p = panchangamViewModel.calculatePanchangam(lat, lng, timezone, sd)
            val masaName = p.masa.nameEnglish
            if (masaName in sacredNames) {
                spans.getOrPut(masaName) { mutableListOf() }.add(i)
            }
        }

        // Convert to date ranges — each masa may appear as a contiguous run of days
        val ranges = mutableListOf<SacredMonthDateRange>()
        for (info in SacredMonthData.getAllSacredMonths()) {
            val days = spans[info.masaNameEnglish] ?: continue

            // Find contiguous runs (a masa could appear twice: this year + next year)
            val runs = mutableListOf<Pair<Int, Int>>() // start, end offsets
            var runStart = days[0]
            var prev = days[0]
            for (j in 1 until days.size) {
                if (days[j] - prev > 2) { // gap > 2 days means new occurrence
                    runs.add(runStart to prev)
                    runStart = days[j]
                }
                prev = days[j]
            }
            runs.add(runStart to prev)

            for ((start, end) in runs) {
                val startDate = today.plus(start, DateTimeUnit.DAY)
                val endDate = today.plus(end, DateTimeUnit.DAY)
                val startMonth = startDate.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
                val endMonth = endDate.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
                val startStr = "$startMonth ${startDate.dayOfMonth}, ${startDate.year}"
                val endStr = "$endMonth ${endDate.dayOfMonth}, ${endDate.year}"
                val isActive = start <= 0 && end >= 0

                ranges.add(SacredMonthDateRange(
                    info = info,
                    startDate = startStr,
                    endDate = endStr,
                    daysUntilStart = if (start > 0) start else -1,
                    daysRemaining = if (isActive) end else -1,
                    isActive = isActive,
                ))
            }
        }

        _sacredMonthRanges.value = ranges.sortedBy { it.daysUntilStart.let { d -> if (d < 0) 0 else d } }
    }
}
