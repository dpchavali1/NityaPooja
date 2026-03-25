package com.nityapooja.shared.ui.sacredmonth

import androidx.lifecycle.ViewModel
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.data.sacredmonth.SacredMonthData
import com.nityapooja.shared.data.sacredmonth.SacredMonthInfo
import com.nityapooja.shared.ui.panchangam.PanchangamViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SacredMonthViewModel(
    private val preferencesManager: UserPreferencesManager,
) : ViewModel() {

    private val panchangamViewModel = PanchangamViewModel(preferencesManager)

    private val _currentSacredMonth = MutableStateFlow<SacredMonthInfo?>(null)
    val currentSacredMonth: StateFlow<SacredMonthInfo?> = _currentSacredMonth.asStateFlow()

    private val _allSacredMonths = MutableStateFlow(SacredMonthData.getAllSacredMonths())
    val allSacredMonths: StateFlow<List<SacredMonthInfo>> = _allSacredMonths.asStateFlow()

    fun detectCurrentMonth(lat: Double, lng: Double, timezone: String) {
        val panchangam = panchangamViewModel.calculatePanchangam(lat, lng, timezone)
        _currentSacredMonth.value = SacredMonthData.getCurrentSacredMonth(panchangam.masa.nameEnglish)
    }
}
