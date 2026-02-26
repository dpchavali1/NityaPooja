package com.nityapooja.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deities")
data class DeityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val nameTelugu: String,
    val imageUrl: String? = null,
    val description: String? = null,
    val descriptionTelugu: String? = null,
    val dayOfWeek: String? = null,
    val colorTheme: String? = null,
    val imageResName: String? = null,
)
