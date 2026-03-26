package com.nityapooja.shared.ui.sacredmonth

import androidx.lifecycle.ViewModel
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.data.sacredmonth.SacredMonthData
import com.nityapooja.shared.data.sacredmonth.SacredMonthDateRange
import com.nityapooja.shared.data.sacredmonth.SacredMonthInfo
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
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

    val isLoading: StateFlow<Boolean> = MutableStateFlow(false)

    fun detectCurrentMonth(lat: Double, lng: Double, timezone: String) {
        val panchangam = panchangamViewModel.calculatePanchangam(lat, lng, timezone)
        _currentSacredMonth.value = SacredMonthData.getCurrentSacredMonth(panchangam.masa.nameEnglish)
        _sacredMonthRanges.value = buildDateRanges()
    }

    /**
     * Hardcoded sacred month dates for 2025-2027.
     * Lunar months are astronomically predictable — no need for expensive runtime calculation.
     * These dates are based on Amanta (new moon ending) system used in AP/Telangana.
     */
    private fun buildDateRanges(): List<SacredMonthDateRange> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        data class MonthDate(val masaName: String, val start: String, val end: String)

        val allDates = listOf(
            // 2025-2026
            MonthDate("Chaitra", "2025-03-30", "2025-04-27"),
            MonthDate("Shravana", "2025-07-25", "2025-08-23"),
            MonthDate("Karthika", "2025-10-22", "2025-11-20"),
            MonthDate("Margashira", "2025-11-21", "2025-12-19"),
            // 2026-2027
            MonthDate("Chaitra", "2026-03-19", "2026-04-17"),
            MonthDate("Shravana", "2026-07-14", "2026-08-12"),
            MonthDate("Karthika", "2026-10-11", "2026-11-09"),
            MonthDate("Margashira", "2026-11-10", "2026-12-09"),
            // 2027-2028
            MonthDate("Chaitra", "2027-04-07", "2027-05-06"),
            MonthDate("Shravana", "2027-08-02", "2027-08-31"),
            MonthDate("Karthika", "2027-10-30", "2027-11-28"),
            MonthDate("Margashira", "2027-11-29", "2027-12-28"),
        )

        val ranges = mutableListOf<SacredMonthDateRange>()
        for (md in allDates) {
            val info = SacredMonthData.getCurrentSacredMonth(md.masaName) ?: continue
            val start = LocalDate.parse(md.start)
            val end = LocalDate.parse(md.end)
            val todayEpoch = today.toEpochDays()
            val startEpoch = start.toEpochDays()
            val endEpoch = end.toEpochDays()

            // Skip if entirely in the past
            if (endEpoch < todayEpoch) continue

            val daysUntilStart = (startEpoch - todayEpoch).toInt()
            val daysRemaining = (endEpoch - todayEpoch).toInt()
            val isActive = daysUntilStart <= 0 && daysRemaining >= 0

            val fmt = { d: LocalDate ->
                val m = d.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
                "$m ${d.dayOfMonth}, ${d.year}"
            }

            ranges.add(SacredMonthDateRange(
                info = info,
                startDate = fmt(start),
                endDate = fmt(end),
                daysUntilStart = if (daysUntilStart > 0) daysUntilStart else -1,
                daysRemaining = if (isActive) daysRemaining else -1,
                isActive = isActive,
            ))
        }
        return ranges.sortedBy { it.daysUntilStart.let { d -> if (d < 0) 0 else d } }
    }
}
