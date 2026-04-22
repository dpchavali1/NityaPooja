package com.nityapooja.shared.ui.family

import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.sin

object TithiUtils {

    private val TITHI_NAMES_TEL = listOf(
        "పాడ్యమి", "విదియ", "తదియ", "చవితి", "పంచమి",
        "షష్ఠి", "సప్తమి", "అష్టమి", "నవమి", "దశమి",
        "ఏకాదశి", "ద్వాదశి", "త్రయోదశి", "చతుర్దశి", "పౌర్ణమి",
        "పాడ్యమి", "విదియ", "తదియ", "చవితి", "పంచమి",
        "షష్ఠి", "సప్తమి", "అష్టమి", "నవమి", "దశమి",
        "ఏకాదశి", "ద్వాదశి", "త్రయోదశి", "చతుర్దశి", "అమావాస్య"
    )

    private fun toRad(deg: Double) = deg * PI / 180.0
    private fun norm360(x: Double) = ((x % 360.0) + 360.0) % 360.0

    /** Days since J2000.0 for a given Gregorian date (noon UTC) */
    private fun daysSinceJ2000(year: Int, month: Int, day: Int): Double {
        // Julian Day Number calculation
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        val jdn = day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
        return jdn.toDouble() - 2451545.0  // JD of J2000.0 = 2451545.0
    }

    /** Returns tithi index 0–29 for a given date */
    fun getTithiIndex(year: Int, month: Int, day: Int): Int {
        val D = daysSinceJ2000(year, month, day)
        // Sun longitude (simplified)
        val sunL = norm360(280.460 + 0.9856474 * D)
        val sunG = toRad(norm360(357.528 + 0.9856003 * D))
        val sunLon = norm360(sunL + 1.915 * sin(sunG) + 0.020 * sin(2 * sunG))
        // Moon longitude (simplified)
        val moonL = norm360(218.316 + 13.176396 * D)
        val moonM = toRad(norm360(134.963 + 13.064993 * D))
        val moonLon = norm360(moonL + 6.289 * sin(moonM))
        // Tithi from elongation
        val elongation = norm360(moonLon - sunLon)
        return floor(elongation / 12.0).toInt().coerceIn(0, 29)
    }

    /** Telugu name for a tithi index */
    fun tithiName(index: Int): String = TITHI_NAMES_TEL.getOrElse(index) { "తిథి" }

    /** Paksha name for a tithi index */
    fun paksha(index: Int): String = if (index < 15) "శుక్ల" else "కృష్ణ"

    /** Full tithi label: paksha + tithi, e.g. "కృష్ణ పక్ష నవమి" */
    fun fullTithiName(index: Int): String = "${paksha(index)} పక్ష ${tithiName(index)}"

    /**
     * Find the Gregorian date (as Triple<year,month,day>) when [tithiIndex] occurs
     * in [targetYear], searching around [refMonth]/[refDay] ± 25 days.
     * Returns null if not found.
     */
    fun findTithiDateInYear(
        tithiIndex: Int,
        refMonth: Int,
        refDay: Int,
        targetYear: Int,
    ): Triple<Int, Int, Int>? {
        // Build search start: go 25 days before the approximate anniversary date
        val startJD = daysSinceJ2000(targetYear, refMonth, refDay) - 25
        for (offset in 0..50) {
            val jd = startJD + offset
            // Convert JD back to calendar date
            val cal = jdToCalendar(jd + 2451545.0)
            val idx = getTithiIndex(cal.first, cal.second, cal.third)
            if (idx == tithiIndex) return cal
        }
        return null
    }

    /** Convert Julian Day Number to Gregorian (year, month, day) */
    private fun jdToCalendar(jd: Double): Triple<Int, Int, Int> {
        val z = floor(jd + 0.5).toLong()
        val a = if (z < 2299161) z else {
            val alpha = floor((z - 1867216.25) / 36524.25).toLong()
            z + 1 + alpha - alpha / 4
        }
        val b = a + 1524
        val c = floor((b - 122.1) / 365.25).toLong()
        val d = floor(365.25 * c).toLong()
        val e = floor((b - d) / 30.6001).toLong()
        val day = (b - d - floor(30.6001 * e)).toInt()
        val month = if (e < 14) (e - 1).toInt() else (e - 13).toInt()
        val year = if (month > 2) (c - 4716).toInt() else (c - 4715).toInt()
        return Triple(year, month, day)
    }
}
