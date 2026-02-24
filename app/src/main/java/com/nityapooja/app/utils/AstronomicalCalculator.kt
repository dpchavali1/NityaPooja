package com.nityapooja.app.utils

import kotlin.math.*

/**
 * Shared astronomical calculation engine for Jyotish features.
 * All longitude functions return TROPICAL degrees. Callers should subtract
 * lahiriAyanamsa() to get sidereal (Nirayana) values.
 *
 * Accuracy: Sun/Moon ~0.02 deg, outer planets ~1-2 deg, inner planets ~2-3 deg.
 * Sufficient for general birth chart and panchangam purposes.
 */
object AstronomicalCalculator {

    // ═══════════════════════════════════════════════════════════
    // Julian Day
    // ═══════════════════════════════════════════════════════════

    fun julianDay(year: Int, month: Int, day: Int, utHours: Double): Double {
        var y = year
        var m = month
        if (m <= 2) { y -= 1; m += 12 }
        val a = y / 100
        val b = 2 - a + a / 4
        return (365.25 * (y + 4716)).toInt() +
                (30.6001 * (m + 1)).toInt() +
                day + utHours / 24.0 + b - 1524.5
    }

    fun centuriesSinceJ2000(jd: Double): Double = (jd - 2451545.0) / 36525.0

    // ═══════════════════════════════════════════════════════════
    // Lahiri Ayanamsa (Chitrapaksha — Govt. of India)
    // ═══════════════════════════════════════════════════════════

    fun lahiriAyanamsa(jd: Double): Double {
        val t = centuriesSinceJ2000(jd)
        return 23.853 + 1.396628 * t + 0.000308 * t * t
    }

    fun siderealLongitude(tropicalLong: Double, jd: Double): Double {
        return normalize360(tropicalLong - lahiriAyanamsa(jd))
    }

    // ═══════════════════════════════════════════════════════════
    // Sun longitude (Meeus — tropical)
    // ═══════════════════════════════════════════════════════════

    fun sunLongitude(jd: Double): Double {
        val t = centuriesSinceJ2000(jd)
        val l0 = normalize360(280.46646 + 36000.76983 * t + 0.0003032 * t * t)
        val m = normalize360(357.52911 + 35999.05029 * t - 0.0001537 * t * t)
        val mRad = Math.toRadians(m)
        val c = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(mRad) +
                (0.019993 - 0.000101 * t) * sin(2 * mRad) +
                0.000289 * sin(3 * mRad)
        val sunTrueLong = l0 + c
        val omega = 125.04 - 1934.136 * t
        val apparent = sunTrueLong - 0.00569 - 0.00478 * sin(Math.toRadians(omega))
        return normalize360(apparent)
    }

    // ═══════════════════════════════════════════════════════════
    // Moon longitude (Meeus — tropical)
    // ═══════════════════════════════════════════════════════════

    fun moonLongitude(jd: Double): Double {
        val t = centuriesSinceJ2000(jd)
        val lPrime = normalize360(218.3165 + 481267.8813 * t)
        val d = normalize360(297.8502 + 445267.1115 * t)
        val dRad = Math.toRadians(d)
        val m = normalize360(357.5291 + 35999.0503 * t)
        val mRad = Math.toRadians(m)
        val mPrime = normalize360(134.9634 + 477198.8676 * t)
        val mPrimeRad = Math.toRadians(mPrime)
        val f = normalize360(93.2720 + 483202.0175 * t)
        val fRad = Math.toRadians(f)

        return normalize360(
            lPrime +
                    6.289 * sin(mPrimeRad) +
                    1.274 * sin(2 * dRad - mPrimeRad) +
                    0.658 * sin(2 * dRad) +
                    0.214 * sin(2 * mPrimeRad) +
                    -0.186 * sin(mRad) +
                    -0.114 * sin(2 * fRad) +
                    0.059 * sin(2 * dRad - 2 * mPrimeRad) +
                    0.057 * sin(2 * dRad - mRad - mPrimeRad) +
                    0.053 * sin(2 * dRad + mPrimeRad) +
                    0.046 * sin(2 * dRad - mRad) +
                    -0.041 * sin(mRad - mPrimeRad) +
                    -0.035 * sin(dRad) +
                    -0.030 * sin(mRad + mPrimeRad)
        )
    }

    // ═══════════════════════════════════════════════════════════
    // Rahu (Mean Ascending Lunar Node) — tropical
    // ═══════════════════════════════════════════════════════════

    fun rahuLongitude(jd: Double): Double {
        val t = centuriesSinceJ2000(jd)
        return normalize360(125.0445479 - 1934.1362891 * t + 0.0020754 * t * t)
    }

    fun ketuLongitude(jd: Double): Double = normalize360(rahuLongitude(jd) + 180.0)

    // ═══════════════════════════════════════════════════════════
    // Mars longitude (simplified Meeus — tropical geocentric)
    // ═══════════════════════════════════════════════════════════

    fun marsLongitude(jd: Double): Double {
        val t = centuriesSinceJ2000(jd)
        // Mars heliocentric mean longitude and anomaly
        val l = normalize360(355.433275 + 19140.2993313 * t)
        val m = normalize360(19.373730 + 19139.8585206 * t)
        val mRad = Math.toRadians(m)
        // Equation of center
        val c = 10.6912 * sin(mRad) + 0.6228 * sin(2 * mRad) + 0.0503 * sin(3 * mRad)
        val helioLong = normalize360(l + c)
        // Approximate geocentric correction using Earth's position
        return helioToGeo(helioLong, 1.5237, jd) // Mars semi-major axis ~1.524 AU
    }

    // ═══════════════════════════════════════════════════════════
    // Jupiter longitude (simplified — tropical geocentric)
    // ═══════════════════════════════════════════════════════════

    fun jupiterLongitude(jd: Double): Double {
        val t = centuriesSinceJ2000(jd)
        val l = normalize360(34.351484 + 3034.9056746 * t)
        val m = normalize360(20.020564 + 3034.6874893 * t)
        val mRad = Math.toRadians(m)
        val c = 5.5549 * sin(mRad) + 0.1683 * sin(2 * mRad) + 0.0071 * sin(3 * mRad)
        val helioLong = normalize360(l + c)
        return helioToGeo(helioLong, 5.2026, jd)
    }

    // ═══════════════════════════════════════════════════════════
    // Saturn longitude (simplified — tropical geocentric)
    // ═══════════════════════════════════════════════════════════

    fun saturnLongitude(jd: Double): Double {
        val t = centuriesSinceJ2000(jd)
        val l = normalize360(50.077444 + 1222.1137943 * t)
        val m = normalize360(317.020774 + 1222.1140000 * t)
        val mRad = Math.toRadians(m)
        val c = 6.3585 * sin(mRad) + 0.2204 * sin(2 * mRad) + 0.0106 * sin(3 * mRad)
        val helioLong = normalize360(l + c)
        return helioToGeo(helioLong, 9.5547, jd)
    }

    // ═══════════════════════════════════════════════════════════
    // Mercury longitude (inner planet — tropical geocentric)
    // ═══════════════════════════════════════════════════════════

    fun mercuryLongitude(jd: Double): Double {
        val t = centuriesSinceJ2000(jd)
        val l = normalize360(252.250906 + 149472.6746358 * t)
        val m = normalize360(174.794787 + 149472.5153900 * t)
        val mRad = Math.toRadians(m)
        val c = 23.4400 * sin(mRad) + 2.9818 * sin(2 * mRad) + 0.5255 * sin(3 * mRad)
        val helioLong = normalize360(l + c)
        return innerPlanetGeo(helioLong, 0.3871, jd) // Mercury semi-major axis ~0.387 AU
    }

    // ═══════════════════════════════════════════════════════════
    // Venus longitude (inner planet — tropical geocentric)
    // ═══════════════════════════════════════════════════════════

    fun venusLongitude(jd: Double): Double {
        val t = centuriesSinceJ2000(jd)
        val l = normalize360(181.979801 + 58517.8156760 * t)
        val m = normalize360(50.416772 + 58517.8038239 * t)
        val mRad = Math.toRadians(m)
        val c = 0.7758 * sin(mRad) + 0.0033 * sin(2 * mRad)
        val helioLong = normalize360(l + c)
        return innerPlanetGeo(helioLong, 0.7233, jd) // Venus semi-major axis ~0.723 AU
    }

    // ═══════════════════════════════════════════════════════════
    // Heliocentric to geocentric conversion (outer planets)
    // Uses simplified approach: planet at distance R, Earth at 1 AU
    // ═══════════════════════════════════════════════════════════

    private fun helioToGeo(helioLongPlanet: Double, semiMajorAxis: Double, jd: Double): Double {
        // Earth's heliocentric longitude ≈ Sun's geocentric longitude + 180
        val earthHelioLong = normalize360(sunLongitude(jd) + 180.0)
        val earthHelioRad = Math.toRadians(earthHelioLong)
        val planetRad = Math.toRadians(helioLongPlanet)

        // Simplified geocentric: treat orbits as circular at mean distance
        val r = semiMajorAxis // planet's distance in AU
        val x = r * cos(planetRad) - cos(earthHelioRad)
        val y = r * sin(planetRad) - sin(earthHelioRad)

        return normalize360(Math.toDegrees(atan2(y, x)))
    }

    // ═══════════════════════════════════════════════════════════
    // Inner planet geocentric conversion (Mercury, Venus)
    // Inner planets orbit closer to Sun than Earth
    // ═══════════════════════════════════════════════════════════

    private fun innerPlanetGeo(helioLongPlanet: Double, semiMajorAxis: Double, jd: Double): Double {
        val earthHelioLong = normalize360(sunLongitude(jd) + 180.0)
        val earthRad = Math.toRadians(earthHelioLong)
        val planetRad = Math.toRadians(helioLongPlanet)

        val r = semiMajorAxis
        val x = r * cos(planetRad) - cos(earthRad)
        val y = r * sin(planetRad) - sin(earthRad)

        return normalize360(Math.toDegrees(atan2(y, x)))
    }

    // ═══════════════════════════════════════════════════════════
    // Lagna (Ascendant) — the rising point of the ecliptic
    // Returns tropical longitude; caller subtracts ayanamsa
    // ═══════════════════════════════════════════════════════════

    fun ascendantLongitude(jd: Double, latitude: Double, longitude: Double): Double {
        val t = centuriesSinceJ2000(jd)

        // Greenwich Mean Sidereal Time (in degrees)
        val gmst = normalize360(
            280.46061837 + 360.98564736629 * (jd - 2451545.0) +
                    0.000387933 * t * t - t * t * t / 38710000.0
        )

        // Local Sidereal Time
        val lst = normalize360(gmst + longitude)
        val lstRad = Math.toRadians(lst)

        // Obliquity of ecliptic
        val obliquity = 23.439291 - 0.0130042 * t
        val oblRad = Math.toRadians(obliquity)
        val latRad = Math.toRadians(latitude)

        // Ascendant formula
        val y = -cos(lstRad)
        val x = sin(oblRad) * tan(latRad) + cos(oblRad) * sin(lstRad)
        var asc = Math.toDegrees(atan2(y, x))

        return normalize360(asc)
    }

    // ═══════════════════════════════════════════════════════════
    // All 9 Navagraha positions (sidereal)
    // Returns list: [Sun, Moon, Mars, Mercury, Jupiter, Venus, Saturn, Rahu, Ketu]
    // ═══════════════════════════════════════════════════════════

    data class GrahaSiderealPosition(
        val grahaIndex: Int,         // 0=Sun...8=Ketu
        val siderealLongitude: Double,
        val rashiIndex: Int,         // 0-11
        val degreesInRashi: Double,  // 0-30
        val nakshatraIndex: Int,     // 0-26
        val nakshatraPada: Int,      // 1-4
    )

    fun allGrahaPositions(jd: Double): List<GrahaSiderealPosition> {
        val ayanamsa = lahiriAyanamsa(jd)
        val tropicalLongs = listOf(
            sunLongitude(jd),
            moonLongitude(jd),
            marsLongitude(jd),
            mercuryLongitude(jd),
            jupiterLongitude(jd),
            venusLongitude(jd),
            saturnLongitude(jd),
            rahuLongitude(jd),
            ketuLongitude(jd),
        )

        return tropicalLongs.mapIndexed { index, tropLong ->
            val sidLong = normalize360(tropLong - ayanamsa)
            val rashiIndex = (sidLong / 30.0).toInt().coerceIn(0, 11)
            val degreesInRashi = sidLong % 30.0
            val nakshatraDeg = 360.0 / 27.0
            val nakshatraIndex = (sidLong / nakshatraDeg).toInt().coerceIn(0, 26)
            val degreesInNakshatra = sidLong % nakshatraDeg
            val pada = (degreesInNakshatra / (nakshatraDeg / 4.0)).toInt().coerceIn(0, 3) + 1

            GrahaSiderealPosition(
                grahaIndex = index,
                siderealLongitude = sidLong,
                rashiIndex = rashiIndex,
                degreesInRashi = degreesInRashi,
                nakshatraIndex = nakshatraIndex,
                nakshatraPada = pada,
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // Utility
    // ═══════════════════════════════════════════════════════════

    fun normalize360(degrees: Double): Double {
        var d = degrees % 360.0
        if (d < 0) d += 360.0
        return d
    }

    fun formatTime(decimalHours: Double): String {
        val clamped = decimalHours.coerceIn(0.0, 23.999)
        val hours = clamped.toInt()
        val minutes = ((clamped - hours) * 60 + 0.5).toInt().coerceIn(0, 59)
        val displayHour: Int
        val amPm: String
        if (hours < 12) {
            displayHour = if (hours == 0) 12 else hours
            amPm = "AM"
        } else {
            displayHour = if (hours == 12) 12 else hours - 12
            amPm = "PM"
        }
        return "%d:%02d %s".format(displayHour, minutes, amPm)
    }
}
