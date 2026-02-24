package com.nityapooja.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shlokas")
data class ShlokaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val textSanskrit: String,
    val textTelugu: String? = null,
    val meaningTelugu: String? = null,
    val meaningEnglish: String? = null,
    val source: String? = null,
    val dayOfYear: Int = 0,
)
