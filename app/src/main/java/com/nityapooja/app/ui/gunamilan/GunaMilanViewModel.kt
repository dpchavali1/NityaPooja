package com.nityapooja.app.ui.gunamilan

import androidx.lifecycle.ViewModel
import com.nityapooja.app.utils.AshtaKootaCalculator
import com.nityapooja.app.utils.AstronomicalCalculator
import com.nityapooja.app.utils.JyotishConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class PersonResult(
    val name: String,
    val nakshatraIndex: Int,
    val nakshatraTelugu: String,
    val nakshatraEnglish: String,
    val rashiIndex: Int,
    val rashiTelugu: String,
    val rashiEnglish: String,
    val pada: Int,
)

data class GunaMilanUiState(
    val brideResult: PersonResult? = null,
    val groomResult: PersonResult? = null,
    val milanResult: AshtaKootaCalculator.GunaMilanResult? = null,
    val isCalculating: Boolean = false,
)

@HiltViewModel
class GunaMilanViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(GunaMilanUiState())
    val uiState: StateFlow<GunaMilanUiState> = _uiState.asStateFlow()

    fun calculateCompatibility(
        brideName: String,
        brideYear: Int, brideMonth: Int, brideDay: Int,
        brideHour: Int, brideMinute: Int,
        brideLat: Double, brideLng: Double, brideTzOffset: Double,
        groomName: String,
        groomYear: Int, groomMonth: Int, groomDay: Int,
        groomHour: Int, groomMinute: Int,
        groomLat: Double, groomLng: Double, groomTzOffset: Double,
    ) {
        _uiState.value = GunaMilanUiState(isCalculating = true)

        // Calculate Moon position for bride
        val brideUtHours = brideHour + brideMinute / 60.0 - brideTzOffset
        val brideJd = AstronomicalCalculator.julianDay(brideYear, brideMonth, brideDay, brideUtHours)
        val brideGrahas = AstronomicalCalculator.allGrahaPositions(brideJd)
        val brideMoon = brideGrahas[1] // Moon = index 1

        // Calculate Moon position for groom
        val groomUtHours = groomHour + groomMinute / 60.0 - groomTzOffset
        val groomJd = AstronomicalCalculator.julianDay(groomYear, groomMonth, groomDay, groomUtHours)
        val groomGrahas = AstronomicalCalculator.allGrahaPositions(groomJd)
        val groomMoon = groomGrahas[1] // Moon = index 1

        val brideResult = PersonResult(
            name = brideName.ifBlank { "వధువు" },
            nakshatraIndex = brideMoon.nakshatraIndex,
            nakshatraTelugu = JyotishConstants.NAKSHATRA_NAMES_TELUGU[brideMoon.nakshatraIndex],
            nakshatraEnglish = JyotishConstants.NAKSHATRA_NAMES_ENGLISH[brideMoon.nakshatraIndex],
            rashiIndex = brideMoon.rashiIndex,
            rashiTelugu = JyotishConstants.RASHI_NAMES_TELUGU[brideMoon.rashiIndex],
            rashiEnglish = JyotishConstants.RASHI_NAMES_ENGLISH[brideMoon.rashiIndex],
            pada = brideMoon.nakshatraPada,
        )

        val groomResult = PersonResult(
            name = groomName.ifBlank { "వరుడు" },
            nakshatraIndex = groomMoon.nakshatraIndex,
            nakshatraTelugu = JyotishConstants.NAKSHATRA_NAMES_TELUGU[groomMoon.nakshatraIndex],
            nakshatraEnglish = JyotishConstants.NAKSHATRA_NAMES_ENGLISH[groomMoon.nakshatraIndex],
            rashiIndex = groomMoon.rashiIndex,
            rashiTelugu = JyotishConstants.RASHI_NAMES_TELUGU[groomMoon.rashiIndex],
            rashiEnglish = JyotishConstants.RASHI_NAMES_ENGLISH[groomMoon.rashiIndex],
            pada = groomMoon.nakshatraPada,
        )

        // Calculate Ashta Koota
        val milanResult = AshtaKootaCalculator.calculate(
            brideNakshatra = brideMoon.nakshatraIndex,
            brideRashi = brideMoon.rashiIndex,
            groomNakshatra = groomMoon.nakshatraIndex,
            groomRashi = groomMoon.rashiIndex,
        )

        _uiState.value = GunaMilanUiState(
            brideResult = brideResult,
            groomResult = groomResult,
            milanResult = milanResult,
        )
    }
}
