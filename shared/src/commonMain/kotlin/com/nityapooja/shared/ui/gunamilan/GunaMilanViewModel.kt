package com.nityapooja.shared.ui.gunamilan

import androidx.lifecycle.ViewModel
import com.nityapooja.shared.utils.AshtaKootaCalculator
import com.nityapooja.shared.utils.AstronomicalCalculator
import com.nityapooja.shared.utils.JyotishConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

data class TeluguCompatibilityCheck(
    val titleTelugu: String,
    val titleEnglish: String,
    val passed: Boolean,
    val detailsTelugu: String,
    val detailsEnglish: String,
    val critical: Boolean = false,
)

data class GunaMilanUiState(
    val brideResult: PersonResult? = null,
    val groomResult: PersonResult? = null,
    val milanResult: AshtaKootaCalculator.GunaMilanResult? = null,
    val teluguChecks: List<TeluguCompatibilityCheck> = emptyList(),
    val hasCriticalIssues: Boolean = false,
    val isCalculating: Boolean = false,
)

class GunaMilanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GunaMilanUiState())
    val uiState: StateFlow<GunaMilanUiState> = _uiState.asStateFlow()

    fun calculateCompatibility(
        brideName: String,
        brideYear: Int, brideMonth: Int, brideDay: Int,
        brideHour: Int, brideMinute: Int,
        brideLat: Double, brideLng: Double, brideTimezoneId: String, brideTzOffset: Double,
        groomName: String,
        groomYear: Int, groomMonth: Int, groomDay: Int,
        groomHour: Int, groomMinute: Int,
        groomLat: Double, groomLng: Double, groomTimezoneId: String, groomTzOffset: Double,
    ) {
        _uiState.value = GunaMilanUiState(isCalculating = true)

        // Calculate Moon position for bride
        val brideJd = AstronomicalCalculator.julianDayFromLocalDateTime(
            year = brideYear,
            month = brideMonth,
            day = brideDay,
            hour = brideHour,
            minute = brideMinute,
            timezoneId = brideTimezoneId,
            fallbackUtcOffsetHours = brideTzOffset,
        )
        val brideGrahas = AstronomicalCalculator.allGrahaPositions(brideJd)
        val brideMoon = brideGrahas[1] // Moon = index 1
        val brideMars = brideGrahas[2] // Mars = index 2
        val brideLagna = calculateLagnaRashiIndex(brideJd, brideLat, brideLng)

        // Calculate Moon position for groom
        val groomJd = AstronomicalCalculator.julianDayFromLocalDateTime(
            year = groomYear,
            month = groomMonth,
            day = groomDay,
            hour = groomHour,
            minute = groomMinute,
            timezoneId = groomTimezoneId,
            fallbackUtcOffsetHours = groomTzOffset,
        )
        val groomGrahas = AstronomicalCalculator.allGrahaPositions(groomJd)
        val groomMoon = groomGrahas[1] // Moon = index 1
        val groomMars = groomGrahas[2] // Mars = index 2
        val groomLagna = calculateLagnaRashiIndex(groomJd, groomLat, groomLng)

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

        val teluguChecks = buildTeluguChecks(
            brideMoonNakshatra = brideMoon.nakshatraIndex,
            brideMoonRashi = brideMoon.rashiIndex,
            bridePada = brideMoon.nakshatraPada,
            brideMarsRashi = brideMars.rashiIndex,
            brideLagnaRashi = brideLagna,
            groomMoonNakshatra = groomMoon.nakshatraIndex,
            groomMoonRashi = groomMoon.rashiIndex,
            groomPada = groomMoon.nakshatraPada,
            groomMarsRashi = groomMars.rashiIndex,
            groomLagnaRashi = groomLagna,
        )
        val hasCriticalIssues = teluguChecks.any { !it.passed && it.critical }

        _uiState.value = GunaMilanUiState(
            brideResult = brideResult,
            groomResult = groomResult,
            milanResult = milanResult,
            teluguChecks = teluguChecks,
            hasCriticalIssues = hasCriticalIssues,
        )
    }

    private fun calculateLagnaRashiIndex(jd: Double, lat: Double, lng: Double): Int {
        val tropicalLagna = AstronomicalCalculator.ascendantLongitude(jd, lat, lng)
        val siderealLagna = AstronomicalCalculator.siderealLongitude(tropicalLagna, jd)
        return (siderealLagna / 30.0).toInt().coerceIn(0, 11)
    }

    private fun buildTeluguChecks(
        brideMoonNakshatra: Int,
        brideMoonRashi: Int,
        bridePada: Int,
        brideMarsRashi: Int,
        brideLagnaRashi: Int,
        groomMoonNakshatra: Int,
        groomMoonRashi: Int,
        groomPada: Int,
        groomMarsRashi: Int,
        groomLagnaRashi: Int,
    ): List<TeluguCompatibilityCheck> {
        val checks = mutableListOf<TeluguCompatibilityCheck>()

        // 1) Ek Nakshatra - Ek Pada is generally avoided.
        val sameNakshatraSamePada = brideMoonNakshatra == groomMoonNakshatra && bridePada == groomPada
        checks += TeluguCompatibilityCheck(
            titleTelugu = "ఏక నక్షత్ర పాదం",
            titleEnglish = "Same Nakshatra Pada",
            passed = !sameNakshatraSamePada,
            detailsTelugu = if (sameNakshatraSamePada) {
                "వధూవరులిద్దరికీ ఒకే నక్షత్రం మరియు ఒకే పాదం ఉంది."
            } else {
                "నక్షత్ర/పాద పునరావృతి లేదు."
            },
            detailsEnglish = if (sameNakshatraSamePada) {
                "Bride and groom share the same nakshatra and same pada."
            } else {
                "No same nakshatra-pada repetition."
            },
            critical = true,
        )

        // 2) Rajju matching check.
        val brideRajju = RAJJU_GROUP[brideMoonNakshatra]
        val groomRajju = RAJJU_GROUP[groomMoonNakshatra]
        val rajjuConflict = brideRajju == groomRajju
        checks += TeluguCompatibilityCheck(
            titleTelugu = "రజ్జు",
            titleEnglish = "Rajju",
            passed = !rajjuConflict,
            detailsTelugu = if (rajjuConflict) {
                "ఇద్దరి రజ్జు '${RAJJU_NAMES_TELUGU[brideRajju]}' ఒకటే. శాస్త్రపరంగా జాగ్రత్త అవసరం."
            } else {
                "రజ్జు భేదం ఉంది."
            },
            detailsEnglish = if (rajjuConflict) {
                "Both fall under '${RAJJU_NAMES_ENGLISH[brideRajju]}' Rajju; usually treated as sensitive."
            } else {
                "No Rajju conflict."
            },
            critical = true,
        )

        // 3) Kuja Dosha parity (basic check from Lagna + Moon).
        val brideKuja = hasKujaDosha(brideMarsRashi, brideLagnaRashi, brideMoonRashi)
        val groomKuja = hasKujaDosha(groomMarsRashi, groomLagnaRashi, groomMoonRashi)
        val kujaBalanced = brideKuja == groomKuja
        checks += TeluguCompatibilityCheck(
            titleTelugu = "కుజ దోషం",
            titleEnglish = "Kuja Dosha",
            passed = kujaBalanced,
            detailsTelugu = when {
                brideKuja && groomKuja -> "ఇద్దరికీ కుజ దోష సంకేతాలు ఉన్నాయి; సాధారణంగా పరస్పర రద్దు భావిస్తారు."
                !brideKuja && !groomKuja -> "ఇద్దరికీ కుజ దోషం కనిపించలేదు."
                brideKuja -> "వధువుకు కుజ దోష సంకేతం ఉంది, వరుడికి లేదు."
                else -> "వరుడికి కుజ దోష సంకేతం ఉంది, వధువుకు లేదు."
            },
            detailsEnglish = when {
                brideKuja && groomKuja -> "Both show Kuja indications; often treated as balanced."
                !brideKuja && !groomKuja -> "No Kuja indications for either chart."
                brideKuja -> "Kuja indication for bride only."
                else -> "Kuja indication for groom only."
            },
            critical = brideKuja.xor(groomKuja),
        )

        // 4) Nadi parity (already inside Ashta Koota, but explicit Telugu alert is useful).
        val brideNadi = JyotishConstants.NAKSHATRA_NADI[brideMoonNakshatra]
        val groomNadi = JyotishConstants.NAKSHATRA_NADI[groomMoonNakshatra]
        val nadiConflict = brideNadi == groomNadi
        checks += TeluguCompatibilityCheck(
            titleTelugu = "నాడి",
            titleEnglish = "Nadi",
            passed = !nadiConflict,
            detailsTelugu = if (nadiConflict) "ఇద్దరి నాడి ఒకటే (${JyotishConstants.NADI_NAMES_TELUGU[brideNadi]})." else "నాడి భేదం ఉంది.",
            detailsEnglish = if (nadiConflict) "Both belong to same Nadi (${JyotishConstants.NADI_NAMES_TELUGU[brideNadi]})." else "Nadi is different.",
            critical = nadiConflict,
        )

        return checks
    }

    private fun hasKujaDosha(marsRashi: Int, lagnaRashi: Int, moonRashi: Int): Boolean {
        val doshaHouses = setOf(1, 2, 4, 7, 8, 12)
        val fromLagna = ((marsRashi - lagnaRashi + 12) % 12) + 1
        val fromMoon = ((marsRashi - moonRashi + 12) % 12) + 1
        return fromLagna in doshaHouses || fromMoon in doshaHouses
    }

    companion object {
        // 0=Shiro, 1=Kantha, 2=Nabhi, 3=Kati, 4=Pada
        private val RAJJU_GROUP = intArrayOf(
            0, 1, 2, 3, 4, 3, 2, 1, 0,
            0, 1, 2, 3, 4, 3, 2, 1, 0,
            0, 1, 2, 3, 4, 3, 2, 1, 0
        )

        private val RAJJU_NAMES_TELUGU = arrayOf("శిరో", "కంఠ", "నాభి", "కటి", "పాద")
        private val RAJJU_NAMES_ENGLISH = arrayOf("Shiro", "Kantha", "Nabhi", "Kati", "Pada")
    }
}
