package com.nityapooja.shared.data.grahanam

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class GrahanamData(
    val id: String,               // e.g. "2025_09_07_chandra"
    val type: GrahanamType,       // SURYA or CHANDRA
    val sparthaUtc: Instant,      // First contact (umbral) in UTC
    val madhyamUtc: Instant,      // Mid-eclipse in UTC
    val mokshamUtc: Instant,      // Last contact (umbral) in UTC
    val visibleFromIndia: Boolean,
    val punyakalaMinutes: Int,    // Punyakalam window in minutes
)

enum class GrahanamType { SURYA, CHANDRA }

fun Instant.toLocalFormatted(tz: TimeZone): String {
    val local = this.toLocalDateTime(tz)
    val hour = local.hour
    val minute = local.minute.toString().padStart(2, '0')
    val period = if (hour >= 12) "PM" else "AM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$displayHour:$minute $period"
}
