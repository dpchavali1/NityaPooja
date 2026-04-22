package com.nityapooja.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val badgeType: String, // BadgeType.name
    val unlockedAt: Long,              // epoch ms
    val shownToUser: Boolean = false,  // for new badge notification
)
