package com.nityapooja.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

@Entity(tableName = "reading_history")
data class ReadingHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contentType: String,
    val contentId: Int,
    val title: String,
    val titleTelugu: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
)
