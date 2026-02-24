package com.nityapooja.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "japa_sessions")
data class JapaSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mantraName: String,
    val mantraNameTelugu: String = "",
    val count: Int,
    val malasCompleted: Int,
    val date: String, // yyyy-MM-dd
    val durationSeconds: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
)
