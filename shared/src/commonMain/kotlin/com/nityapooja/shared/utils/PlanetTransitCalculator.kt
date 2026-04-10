package com.nityapooja.shared.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Computes upcoming sidereal rashi transits for outer planets using binary-search
 * on [AstronomicalCalculator.allGrahaPositions].
 *
 * Graha indices used:
 *   4 = Jupiter (Guru)   — prograde, ~1 year per rashi
 *   6 = Saturn  (Sani)   — prograde, ~2.5 years per rashi
 *   7 = Rahu             — retrograde (rashi index decreases over time)
 *   8 = Ketu             — retrograde (always opposite Rahu, moves together)
 *
 * Accuracy: Julian-Day binary search to within ±1 day. The exact time of
 * ingress is approximate (Meeus-based planetary formulae). Do NOT use for
 * astrology chart casting — only for advance notification purposes.
 */
object PlanetTransitCalculator {

    data class TransitEvent(
        val grahaIndex: Int,
        val grahaNameTelugu: String,
        val grahaNameEnglish: String,
        val fromRashiIndex: Int,
        val fromRashiTelugu: String,
        val toRashiIndex: Int,
        val toRashiTelugu: String,
        val epochMillis: Long,   // approximate UTC epoch of the ingress
    )

    // Graha indices to watch for transits
    private val TRANSIT_GRAHA_INDICES = intArrayOf(4, 6, 7, 8) // Guru, Sani, Rahu, Ketu

    // Rahu & Ketu move retrograde — their rashi index decreases over time
    private val RETROGRADE_GRAHAS = setOf(7, 8)

    /**
     * Returns all upcoming transit events within [lookaheadDays] days from today.
     * Typically called once and cached; list is short (4–6 events over 2 years).
     */
    fun getUpcomingTransits(lookaheadDays: Int = 730): List<TransitEvent> {
        val todayUtc = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val todayJd = AstronomicalCalculator.julianDay(
            year = todayUtc.year,
            month = todayUtc.monthNumber,
            day = todayUtc.dayOfMonth,
            utHours = 0.0,
        )
        val endJd = todayJd + lookaheadDays

        val transits = mutableListOf<TransitEvent>()

        for (grahaIdx in TRANSIT_GRAHA_INDICES) {
            transits += findTransitsForGraha(grahaIdx, todayJd, endJd)
        }

        return transits.sortedBy { it.epochMillis }
    }

    private fun findTransitsForGraha(grahaIdx: Int, startJd: Double, endJd: Double): List<TransitEvent> {
        val events = mutableListOf<TransitEvent>()
        val isRetrograde = grahaIdx in RETROGRADE_GRAHAS

        var currentJd = startJd
        var prevRashi = rashiAt(grahaIdx, currentJd)

        // Step in ~10-day increments to detect rashi boundary crossings
        val stepDays = 10.0
        while (currentJd < endJd) {
            val nextJd = (currentJd + stepDays).coerceAtMost(endJd)
            val nextRashi = rashiAt(grahaIdx, nextJd)

            if (nextRashi != prevRashi) {
                // Rashi boundary detected — binary-search to within 1 day
                val transitJd = binarySearchTransit(grahaIdx, currentJd, nextJd, prevRashi)
                val transitMs = jdToEpochMillis(transitJd)
                val fromRashi = prevRashi
                val toRashi = nextRashi

                events += TransitEvent(
                    grahaIndex = grahaIdx,
                    grahaNameTelugu = JyotishConstants.GRAHA_NAMES_TELUGU[grahaIdx],
                    grahaNameEnglish = JyotishConstants.GRAHA_NAMES_ENGLISH[grahaIdx],
                    fromRashiIndex = fromRashi,
                    fromRashiTelugu = JyotishConstants.RASHI_NAMES_TELUGU[fromRashi],
                    toRashiIndex = toRashi,
                    toRashiTelugu = JyotishConstants.RASHI_NAMES_TELUGU[toRashi],
                    epochMillis = transitMs,
                )
                prevRashi = nextRashi
            } else {
                prevRashi = nextRashi
            }

            currentJd = nextJd
        }

        return events
    }

    private fun rashiAt(grahaIdx: Int, jd: Double): Int {
        return AstronomicalCalculator.allGrahaPositions(jd)[grahaIdx].rashiIndex
    }

    /**
     * Binary searches between [loJd] and [hiJd] for the exact day when the graha
     * moves from [prevRashi] to a different rashi. Returns the JD of that crossing.
     */
    private fun binarySearchTransit(grahaIdx: Int, loJd: Double, hiJd: Double, prevRashi: Int): Double {
        var lo = loJd
        var hi = hiJd
        repeat(20) { // ~20 iterations → sub-day accuracy
            val mid = (lo + hi) / 2.0
            val rashi = rashiAt(grahaIdx, mid)
            if (rashi == prevRashi) lo = mid else hi = mid
        }
        return (lo + hi) / 2.0
    }

    /** Converts Julian Day to UTC epoch milliseconds. */
    private fun jdToEpochMillis(jd: Double): Long {
        // JD 2440587.5 = Unix epoch (1970-01-01 00:00 UTC)
        return ((jd - 2440587.5) * 86400.0 * 1000.0).toLong()
    }
}
