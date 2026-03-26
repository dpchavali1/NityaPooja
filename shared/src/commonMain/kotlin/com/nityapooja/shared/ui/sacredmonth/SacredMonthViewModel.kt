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

    fun detectCurrentMonth(lat: Double, lng: Double, timezone: String) {
        // Quick: detect current month (single panchangam calc)
        val panchangam = panchangamViewModel.calculatePanchangam(lat, lng, timezone)
        _currentSacredMonth.value = SacredMonthData.getCurrentSacredMonth(panchangam.masa.nameEnglish)

        // Use cached global results if available, otherwise compute once
        if (cachedRanges != null) {
            _sacredMonthRanges.value = cachedRanges!!
        } else {
            _isLoading.value = true
            viewModelScope.launch {
                val ranges = withContext(Dispatchers.Default) {
                    computeDateRanges()
                }
                cachedRanges = ranges
                _sacredMonthRanges.value = ranges
                _isLoading.value = false
            }
        }
    }

    companion object {
        // Cached across all instances — masa dates are the same for everyone
        // (lunar position doesn't depend on observer location)
        private var cachedRanges: List<SacredMonthDateRange>? = null
    }

    private fun computeDateRanges(): List<SacredMonthDateRange> {
        // Use fixed reference point — masa is astronomical, not location-dependent
        val refLat = 17.385 // Hyderabad
        val refLng = 78.4867
        val refTz = "Asia/Kolkata"
        val today = Clock.System.todayIn(TimeZone.of(refTz))
        val sacredNames = SacredMonthData.getAllSacredMonths().map { it.masaNameEnglish }.toSet()

        // Step 1: Coarse scan every 10 days (only 40 calcs for 400 days)
        val masaDays = mutableMapOf<String, MutableList<Int>>()
        for (i in 0 until 400 step 10) {
            val date = today.plus(i, DateTimeUnit.DAY)
            val sd = SelectedDate(date.year, date.monthNumber, date.dayOfMonth)
            val p = panchangamViewModel.calculatePanchangam(refLat, refLng, refTz, sd)
            if (p.masa.nameEnglish in sacredNames) {
                masaDays.getOrPut(p.masa.nameEnglish) { mutableListOf() }.add(i)
            }
        }

        // Step 2: Fine scan only at boundaries (+-10 days around first/last of each group)
        val refinedSpans = mutableMapOf<String, MutableList<Int>>()
        for ((masaName, coarseDays) in masaDays) {
            val fineDays = mutableSetOf<Int>()
            val groups = mutableListOf<Pair<Int, Int>>()
            var gStart = coarseDays[0]; var prev = coarseDays[0]
            for (j in 1 until coarseDays.size) {
                if (coarseDays[j] - prev > 15) { groups.add(gStart to prev); gStart = coarseDays[j] }
                prev = coarseDays[j]
            }
            groups.add(gStart to prev)

            for ((gs, ge) in groups) {
                // Scan start boundary
                for (d in maxOf(0, gs - 10)..minOf(399, gs + 2)) {
                    val date = today.plus(d, DateTimeUnit.DAY)
                    val sd = SelectedDate(date.year, date.monthNumber, date.dayOfMonth)
                    val p = panchangamViewModel.calculatePanchangam(refLat, refLng, refTz, sd)
                    if (p.masa.nameEnglish == masaName) fineDays.add(d)
                }
                // Scan end boundary
                for (d in maxOf(0, ge - 2)..minOf(399, ge + 10)) {
                    val date = today.plus(d, DateTimeUnit.DAY)
                    val sd = SelectedDate(date.year, date.monthNumber, date.dayOfMonth)
                    val p = panchangamViewModel.calculatePanchangam(refLat, refLng, refTz, sd)
                    if (p.masa.nameEnglish == masaName) fineDays.add(d)
                }
            }
            refinedSpans[masaName] = fineDays.sorted().toMutableList()
        }

        // Step 3: Convert to date ranges
        val ranges = mutableListOf<SacredMonthDateRange>()
        for (info in SacredMonthData.getAllSacredMonths()) {
            val days = refinedSpans[info.masaNameEnglish] ?: continue
            if (days.isEmpty()) continue
            val runs = mutableListOf<Pair<Int, Int>>()
            var runStart = days[0]; var prevDay = days[0]
            for (j in 1 until days.size) {
                if (days[j] - prevDay > 2) { runs.add(runStart to prevDay); runStart = days[j] }
                prevDay = days[j]
            }
            runs.add(runStart to prevDay)

            for ((start, end) in runs) {
                val startDate = today.plus(start, DateTimeUnit.DAY)
                val endDate = today.plus(end, DateTimeUnit.DAY)
                val fmt = { d: kotlinx.datetime.LocalDate ->
                    val m = d.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
                    "$m ${d.dayOfMonth}, ${d.year}"
                }
                val isActive = start <= 0 && end >= 0
                ranges.add(SacredMonthDateRange(
                    info = info, startDate = fmt(startDate), endDate = fmt(endDate),
                    daysUntilStart = if (start > 0) start else -1,
                    daysRemaining = if (isActive) end else -1,
                    isActive = isActive,
                ))
            }
        }
        return ranges.sortedBy { it.daysUntilStart.let { d -> if (d < 0) 0 else d } }
    }
}
