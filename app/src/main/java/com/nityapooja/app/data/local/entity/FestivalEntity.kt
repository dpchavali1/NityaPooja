package com.nityapooja.app.data.local.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Entity(tableName = "festivals")
data class FestivalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val nameTelugu: String,
    val date: String? = null,
    val description: String? = null,
    val descriptionTelugu: String? = null,
    val significance: String? = null,
    val significanceTelugu: String? = null,
    val imageUrl: String? = null,
    val specialAartiId: Int = 0,
    // Store month-day for recurring festivals (MM-dd format)
    val dateThisYear: String? = null,
    val dateNextYear: String? = null,
) {
    /**
     * Returns the upcoming festival date — this year if not passed, next year if done.
     * Also returns the day of week.
     */
    @Ignore
    fun getUpcomingDateInfo(): FestivalDateInfo? {
        val thisYearStr = dateThisYear ?: return null
        val nextYearStr = dateNextYear

        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val thisYearDate = LocalDate.parse(thisYearStr, formatter)
            val today = LocalDate.now()

            val targetDate = if (thisYearDate.isBefore(today)) {
                // Festival has passed this year
                if (nextYearStr != null) {
                    LocalDate.parse(nextYearStr, formatter)
                } else {
                    // Estimate next year same date
                    thisYearDate.plusYears(1)
                }
            } else {
                thisYearDate
            }

            val dayName = targetDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            val displayDate = targetDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            val isPast = thisYearDate.isBefore(today)

            FestivalDateInfo(
                displayDate = displayDate,
                dayOfWeek = dayName,
                isPastThisYear = isPast,
                daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, targetDate).toInt(),
            )
        } catch (e: Exception) {
            null
        }
    }
}

data class FestivalDateInfo(
    val displayDate: String,
    val dayOfWeek: String,
    val isPastThisYear: Boolean,
    val daysUntil: Int,
)
