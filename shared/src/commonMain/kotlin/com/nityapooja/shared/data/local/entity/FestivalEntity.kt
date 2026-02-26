package com.nityapooja.shared.data.local.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

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
    val dateThisYear: String? = null,
    val dateNextYear: String? = null,
) {
    @Ignore
    fun getUpcomingDateInfo(): FestivalDateInfo? {
        val thisYearStr = dateThisYear ?: return null
        val nextYearStr = dateNextYear

        return try {
            val thisYearDate = LocalDate.parse(thisYearStr)
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            val targetDate = if (thisYearDate < today) {
                if (nextYearStr != null) {
                    LocalDate.parse(nextYearStr)
                } else {
                    LocalDate(thisYearDate.year + 1, thisYearDate.monthNumber, thisYearDate.dayOfMonth)
                }
            } else {
                thisYearDate
            }

            val dayName = targetDate.dayOfWeek.name.lowercase()
                .replaceFirstChar { it.uppercase() }
            val monthName = targetDate.month.name.lowercase()
                .replaceFirstChar { it.uppercase() }.take(3)
            val displayDate = "$monthName ${targetDate.dayOfMonth.toString().padStart(2, '0')}, ${targetDate.year}"
            val isPast = thisYearDate < today
            val daysUntil = (targetDate.toEpochDays() - today.toEpochDays()).toInt()

            FestivalDateInfo(
                displayDate = displayDate,
                dayOfWeek = dayName,
                isPastThisYear = isPast,
                daysUntil = daysUntil,
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
