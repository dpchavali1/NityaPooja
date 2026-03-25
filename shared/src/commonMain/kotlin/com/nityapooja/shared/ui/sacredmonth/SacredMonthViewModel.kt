package com.nityapooja.shared.ui.sacredmonth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.data.sacredmonth.SacredMonthData
import com.nityapooja.shared.data.sacredmonth.SacredMonthDateRange
import com.nityapooja.shared.data.sacredmonth.SacredMonthInfo
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import com.nityapooja.shared.ui.panchangam.SelectedDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var computed = false

    fun detectCurrentMonth(lat: Double, lng: Double, timezone: String) {
        // Quick: detect current month on main thread (single panchangam calc)
        val panchangam = panchangamViewModel.calculatePanchangam(lat, lng, timezone)
        _currentSacredMonth.value = SacredMonthData.getCurrentSacredMonth(panchangam.masa.nameEnglish)

        // Heavy: compute date ranges on background thread (only once)
        if (!computed) {
            computed = true
            _isLoading.value = true
            viewModelScope.launch {
                val ranges = withContext(Dispatchers.Default) {
                    computeDateRanges(lat, lng, timezone)
                }
                _sacredMonthRanges.value = ranges
                _isLoading.value = false
            }
        }
    }

    private fun computeDateRanges(lat: Double, lng: Double, timezone: String): List<SacredMonthDateRange> {
        val today = Clock.System.todayIn(TimeZone.of(timezone))
        val sacredNames = SacredMonthData.getAllSacredMonths().map { it.masaNameEnglish }.toSet()

        // Sample every 5 days to find rough boundaries, then narrow down
        val masaDays = mutableMapOf<String, MutableList<Int>>()

        // Coarse scan: every 5 days over 400 days
        for (i in 0 until 400 step 5) {
            val date = today.plus(i, DateTimeUnit.DAY)
            val sd = SelectedDate(date.year, date.monthNumber, date.dayOfMonth)
            val p = panchangamViewModel.calculatePanchangam(lat, lng, timezone, sd)
            val masaName = p.masa.nameEnglish
            if (masaName in sacredNames) {
                masaDays.getOrPut(masaName) { mutableListOf() }.add(i)
            }
        }

        // Fine scan: for each sacred month found, scan +-6 days around boundaries to find exact start/end
        val refinedSpans = mutableMapOf<String, MutableList<Int>>()
        for ((masaName, coarseDays) in masaDays) {
            val fineDays = mutableSetOf<Int>()
            // Find boundary regions (first and last of each contiguous group)
            val groups = mutableListOf<Pair<Int, Int>>()
            var gStart = coarseDays[0]
            var prev = coarseDays[0]
            for (j in 1 until coarseDays.size) {
                if (coarseDays[j] - prev > 10) {
                    groups.add(gStart to prev)
                    gStart = coarseDays[j]
                }
                prev = coarseDays[j]
            }
            groups.add(gStart to prev)

            for ((gs, ge) in groups) {
                // Scan around start boundary
                for (d in maxOf(0, gs - 6)..minOf(399, gs + 6)) {
                    val date = today.plus(d, DateTimeUnit.DAY)
                    val sd = SelectedDate(date.year, date.monthNumber, date.dayOfMonth)
                    val p = panchangamViewModel.calculatePanchangam(lat, lng, timezone, sd)
                    if (p.masa.nameEnglish == masaName) fineDays.add(d)
                }
                // Scan around end boundary
                for (d in maxOf(0, ge - 6)..minOf(399, ge + 6)) {
                    val date = today.plus(d, DateTimeUnit.DAY)
                    val sd = SelectedDate(date.year, date.monthNumber, date.dayOfMonth)
                    val p = panchangamViewModel.calculatePanchangam(lat, lng, timezone, sd)
                    if (p.masa.nameEnglish == masaName) fineDays.add(d)
                }
            }
            refinedSpans[masaName] = fineDays.sorted().toMutableList()
        }

        // Convert to date ranges
        val ranges = mutableListOf<SacredMonthDateRange>()
        for (info in SacredMonthData.getAllSacredMonths()) {
            val days = refinedSpans[info.masaNameEnglish] ?: continue
            if (days.isEmpty()) continue

            val runs = mutableListOf<Pair<Int, Int>>()
            var runStart = days[0]
            var prevDay = days[0]
            for (j in 1 until days.size) {
                if (days[j] - prevDay > 2) {
                    runs.add(runStart to prevDay)
                    runStart = days[j]
                }
                prevDay = days[j]
            }
            runs.add(runStart to prevDay)

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

        return ranges.sortedBy { it.daysUntilStart.let { d -> if (d < 0) 0 else d } }
    }
}
