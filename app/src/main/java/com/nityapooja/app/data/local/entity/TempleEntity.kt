package com.nityapooja.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temples")
data class TempleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val nameTelugu: String,
    val location: String? = null,
    val locationTelugu: String? = null,
    val state: String? = null,
    val youtubeUrl: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timings: String? = null,
    val imageUrl: String? = null,
    val description: String? = null,
    val descriptionTelugu: String? = null,
    val hasLiveDarshan: Boolean = false,
    val bookingUrl: String? = null,
)
