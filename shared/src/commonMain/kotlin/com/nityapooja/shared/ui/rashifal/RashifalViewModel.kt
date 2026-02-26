package com.nityapooja.shared.ui.rashifal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.RashiEntity
import com.nityapooja.shared.data.preferences.UserPreferencesManager
import com.nityapooja.shared.data.repository.DevotionalRepository
import com.nityapooja.shared.utils.AstronomicalCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class RashifalViewModel(
    private val repository: DevotionalRepository,
    preferencesManager: UserPreferencesManager,
) : ViewModel() {

    private data class LocationSettings(
        val lat: Double,
        val lng: Double,
        val timezone: String,
    )

    val rashis: StateFlow<List<RashiEntity>> = repository.getAllRashis()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val locationSettings: StateFlow<LocationSettings> = combine(
        preferencesManager.locationLat,
        preferencesManager.locationLng,
        preferencesManager.locationTimezone,
    ) { lat, lng, timezone ->
        LocationSettings(lat = lat, lng = lng, timezone = timezone)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LocationSettings(17.385, 78.4867, "Asia/Kolkata"),
    )

    private val _selectedRashi = MutableStateFlow<RashiEntity?>(null)
    val selectedRashi: StateFlow<RashiEntity?> = _selectedRashi.asStateFlow()

    fun selectRashi(id: Int) {
        viewModelScope.launch {
            _selectedRashi.value = repository.getRashiById(id).first()
        }
    }

    fun clearSelection() {
        _selectedRashi.value = null
    }

    fun getTodayPrediction(rashi: RashiEntity): Pair<String?, String?> {
        val dayOfWeek = getPanchangamDayOfWeek()
        return when (dayOfWeek) {
            DayOfWeek.SUNDAY -> Pair(rashi.predictionSunTelugu, rashi.predictionSun)
            DayOfWeek.MONDAY -> Pair(rashi.predictionMonTelugu, rashi.predictionMon)
            DayOfWeek.TUESDAY -> Pair(rashi.predictionTueTelugu, rashi.predictionTue)
            DayOfWeek.WEDNESDAY -> Pair(rashi.predictionWedTelugu, rashi.predictionWed)
            DayOfWeek.THURSDAY -> Pair(rashi.predictionThuTelugu, rashi.predictionThu)
            DayOfWeek.FRIDAY -> Pair(rashi.predictionFriTelugu, rashi.predictionFri)
            DayOfWeek.SATURDAY -> Pair(rashi.predictionSatTelugu, rashi.predictionSat)
            else -> Pair(null, null)
        }
    }

    private fun getPanchangamDayOfWeek(): DayOfWeek {
        val location = locationSettings.value
        val tz = try {
            TimeZone.of(location.timezone)
        } catch (_: Exception) {
            TimeZone.currentSystemDefault()
        }

        val now = Clock.System.now()
        val localNow = now.toLocalDateTime(tz)
        val utcOffsetHours = now.offsetIn(tz).totalSeconds / 3600.0
        val sunTimes = AstronomicalCalculator.calculateSunTimesDecimal(
            lat = location.lat,
            lng = location.lng,
            year = localNow.year,
            month = localNow.monthNumber,
            day = localNow.dayOfMonth,
            utcOffsetHours = utcOffsetHours,
        )
        val sunrise = normalize24(sunTimes.sunriseDecimal)
        val currentHours = localNow.hour + localNow.minute / 60.0 + localNow.second / 3600.0
        val panchangamDate = if (currentHours < sunrise) {
            localNow.date.plus(-1, DateTimeUnit.DAY)
        } else {
            localNow.date
        }
        return panchangamDate.dayOfWeek
    }

    private fun normalize24(value: Double): Double {
        var v = value % 24.0
        if (v < 0) v += 24.0
        return v
    }
}
