package com.nityapooja.shared.data.grahanam

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object GrahanamRepository {

    // Hardcoded eclipses 2025–2028 visible from India
    // UTC times are umbral contact (Sparsha = U1, Moksham = U4) per standard eclipse catalogs.
    private val eclipses: List<GrahanamData> = listOf(
        // Sep 7, 2025 — Total Chandra Grahanam (fully visible India)
        // Umbral: U1 17:27 UTC, Greatest 18:11 UTC, U4 18:56 UTC
        GrahanamData(
            id = "2025_09_07_chandra",
            type = GrahanamType.CHANDRA,
            sparthaUtc = Instant.parse("2025-09-07T17:27:00Z"),
            madhyamUtc = Instant.parse("2025-09-07T18:11:00Z"),
            mokshamUtc = Instant.parse("2025-09-07T18:56:00Z"),
            visibleFromIndia = true,
            punyakalaMinutes = 89,
        ),
        // Mar 3, 2026 — Total Chandra Grahanam (visible India at moonrise)
        // Approximate: U1 10:02 UTC, Greatest 11:33 UTC, U4 13:06 UTC
        GrahanamData(
            id = "2026_03_03_chandra",
            type = GrahanamType.CHANDRA,
            sparthaUtc = Instant.parse("2026-03-03T10:02:00Z"),
            madhyamUtc = Instant.parse("2026-03-03T11:33:00Z"),
            mokshamUtc = Instant.parse("2026-03-03T13:06:00Z"),
            visibleFromIndia = true,
            punyakalaMinutes = 184,
        ),
        // Aug 28, 2026 — Partial Chandra Grahanam (visible India)
        // Approximate: U1 19:12 UTC, Greatest 20:13 UTC, U4 21:15 UTC
        GrahanamData(
            id = "2026_08_28_chandra",
            type = GrahanamType.CHANDRA,
            sparthaUtc = Instant.parse("2026-08-28T19:12:00Z"),
            madhyamUtc = Instant.parse("2026-08-28T20:13:00Z"),
            mokshamUtc = Instant.parse("2026-08-28T21:15:00Z"),
            visibleFromIndia = true,
            punyakalaMinutes = 123,
        ),
        // Jul 22, 2028 — Total Surya Grahanam (totality path crosses India — major event)
        // Approximate: C1 01:52 UTC, Greatest 03:23 UTC, C4 04:53 UTC
        GrahanamData(
            id = "2028_07_22_surya",
            type = GrahanamType.SURYA,
            sparthaUtc = Instant.parse("2028-07-22T01:52:00Z"),
            madhyamUtc = Instant.parse("2028-07-22T03:23:00Z"),
            mokshamUtc = Instant.parse("2028-07-22T04:53:00Z"),
            visibleFromIndia = true,
            punyakalaMinutes = 181,
        ),
    )

    /** Returns upcoming eclipses (Sparsha in future), sorted by date. */
    fun getUpcomingGrahanam(from: Instant): List<GrahanamData> =
        eclipses.filter { it.sparthaUtc > from }.sortedBy { it.sparthaUtc }

    /** Returns the next upcoming eclipse, or null if none remain. */
    fun getNextGrahanam(from: Instant): GrahanamData? =
        getUpcomingGrahanam(from).firstOrNull()

    /** Returns true if [grahanam] is scheduled for tomorrow in the given timezone. */
    fun isDayBefore(grahanam: GrahanamData, now: Instant, tz: TimeZone): Boolean {
        val localNow = now.toLocalDateTime(tz).date
        val localSparsha = grahanam.sparthaUtc.toLocalDateTime(tz).date
        return localSparsha.toEpochDays() - localNow.toEpochDays() == 1
    }

    /** Returns true if [grahanam]'s Sparsha falls within [days] calendar days from now. */
    fun isWithinDays(grahanam: GrahanamData, days: Int, now: Instant, tz: TimeZone): Boolean {
        val localNow = now.toLocalDateTime(tz).date
        val localSparsha = grahanam.sparthaUtc.toLocalDateTime(tz).date
        val diff = localSparsha.toEpochDays() - localNow.toEpochDays()
        return diff in 0..days
    }
}
