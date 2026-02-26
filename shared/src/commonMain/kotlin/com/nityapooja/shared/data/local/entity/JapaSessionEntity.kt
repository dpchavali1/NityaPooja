package com.nityapooja.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

@Entity(tableName = "japa_sessions")
data class JapaSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mantraName: String,
    val mantraNameTelugu: String = "",
    val count: Int,
    val malasCompleted: Int,
    val date: String,
    val durationSeconds: Long = 0,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
)
