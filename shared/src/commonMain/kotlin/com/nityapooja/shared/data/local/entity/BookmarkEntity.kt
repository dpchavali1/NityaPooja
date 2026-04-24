package com.nityapooja.shared.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

@Entity(
    tableName = "bookmarks",
    indices = [Index(value = ["contentType", "contentId"], unique = true)]
)
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contentType: String,
    val contentId: Int,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
)
