package com.nityapooja.shared.ui.jataka

import androidx.lifecycle.ViewModel
import com.nityapooja.shared.utils.AstronomicalCalculator
import com.nityapooja.shared.utils.JyotishConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GrahaPosition(
    val grahaIndex: Int,        // 0=Sun..8=Ketu
    val nameTelugu: String,
    val nameEnglish: String,
    val abbreviation: String,   // Telugu short form
    val rashiIndex: Int,        // 0-11
    val rashiTelugu: String,
    val rashiEnglish: String,
    val degreesInRashi: Double,
    val nakshatraIndex: Int,    // 0-26
    val nakshatraTelugu: String,
    val nakshatraEnglish: String,
    val pada: Int,              // 1-4
)

data class JatakaResult(
    val positions: List<GrahaPosition>,
    val lagnaRashiIndex: Int,
    val lagnaRashiTelugu: String,
    val lagnaRashiEnglish: String,
    val lagnaDegreesInRashi: Double,
    val janmaNakshatraIndex: Int,
    val janmaNakshatraTelugu: String,
    val janmaNakshatraEnglish: String,
    val janmaNakshatraPada: Int,
    val janmaRashiIndex: Int,
    val janmaRashiTelugu: String,
    val janmaRashiEnglish: String,
)

data class JatakaUiState(
    val result: JatakaResult? = null,
    val isCalculating: Boolean = false,
)

class JatakaChakramViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(JatakaUiState())
    val uiState: StateFlow<JatakaUiState> = _uiState.asStateFlow()

    fun calculateChart(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        latitude: Double,
        longitude: Double,
        timezoneId: String,
        utcOffsetHours: Double,
    ) {
        _uiState.value = JatakaUiState(isCalculating = true)

        val jd = AstronomicalCalculator.julianDayFromLocalDateTime(
            year = year,
            month = month,
            day = day,
            hour = hour,
            minute = minute,
            timezoneId = timezoneId,
            fallbackUtcOffsetHours = utcOffsetHours,
        )

        // Calculate all 9 graha positions (sidereal)
        val grahaPositions = AstronomicalCalculator.allGrahaPositions(jd)

        // Calculate Lagna (Ascendant)
        val tropicalLagna = AstronomicalCalculator.ascendantLongitude(jd, latitude, longitude)
        val sidLagna = AstronomicalCalculator.siderealLongitude(tropicalLagna, jd)
        val lagnaRashiIndex = (sidLagna / 30.0).toInt().coerceIn(0, 11)
        val lagnaDegreesInRashi = sidLagna % 30.0

        // Build GrahaPosition list
        val positions = grahaPositions.map { gsp ->
            GrahaPosition(
                grahaIndex = gsp.grahaIndex,
                nameTelugu = JyotishConstants.GRAHA_NAMES_TELUGU[gsp.grahaIndex],
                nameEnglish = JyotishConstants.GRAHA_NAMES_ENGLISH[gsp.grahaIndex],
                abbreviation = JyotishConstants.GRAHA_ABBREVIATIONS_TELUGU[gsp.grahaIndex],
                rashiIndex = gsp.rashiIndex,
                rashiTelugu = JyotishConstants.RASHI_NAMES_TELUGU[gsp.rashiIndex],
                rashiEnglish = JyotishConstants.RASHI_NAMES_ENGLISH[gsp.rashiIndex],
                degreesInRashi = gsp.degreesInRashi,
                nakshatraIndex = gsp.nakshatraIndex,
                nakshatraTelugu = JyotishConstants.NAKSHATRA_NAMES_TELUGU[gsp.nakshatraIndex],
                nakshatraEnglish = JyotishConstants.NAKSHATRA_NAMES_ENGLISH[gsp.nakshatraIndex],
                pada = gsp.nakshatraPada,
            )
        }

        // Janma Nakshatra = Moon's nakshatra (index 1 = Moon)
        val moonPos = positions[1]

        val result = JatakaResult(
            positions = positions,
            lagnaRashiIndex = lagnaRashiIndex,
            lagnaRashiTelugu = JyotishConstants.RASHI_NAMES_TELUGU[lagnaRashiIndex],
            lagnaRashiEnglish = JyotishConstants.RASHI_NAMES_ENGLISH[lagnaRashiIndex],
            lagnaDegreesInRashi = lagnaDegreesInRashi,
            janmaNakshatraIndex = moonPos.nakshatraIndex,
            janmaNakshatraTelugu = moonPos.nakshatraTelugu,
            janmaNakshatraEnglish = moonPos.nakshatraEnglish,
            janmaNakshatraPada = moonPos.pada,
            janmaRashiIndex = moonPos.rashiIndex,
            janmaRashiTelugu = moonPos.rashiTelugu,
            janmaRashiEnglish = moonPos.rashiEnglish,
        )

        _uiState.value = JatakaUiState(result = result)
    }
}
