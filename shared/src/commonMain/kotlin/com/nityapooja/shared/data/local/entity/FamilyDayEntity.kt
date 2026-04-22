package com.nityapooja.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

@Entity(tableName = "family_days")
data class FamilyDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personName: String,
    val relation: String = "",
    val type: String = FamilyDayType.BIRTHDAY.name,
    val label: String = "",
    // For BIRTHDAY, ANNIVERSARY, CUSTOM: store month + day (year ignored, repeats annually)
    val gregorianMonth: Int = 0,   // 1–12
    val gregorianDay: Int = 0,     // 1–31
    // For TITHI: store the reference Gregorian date to calculate the tithi
    val tithiRefYear: Int = 0,
    val tithiRefMonth: Int = 0,
    val tithiRefDay: Int = 0,
    val notes: String = "",
    val notifyDayBefore: Boolean = true,
    val notifyOnDay: Boolean = true,
    val isActive: Boolean = true,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
) {
    fun getFamilyDayType(): FamilyDayType =
        runCatching { FamilyDayType.valueOf(type) }.getOrDefault(FamilyDayType.BIRTHDAY)
}
