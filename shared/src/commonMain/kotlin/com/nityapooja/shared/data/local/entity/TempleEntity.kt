package com.nityapooja.shared.data.local.entity

import androidx.room.ColumnInfo
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
    val websiteUrl: String? = null,
    // v2 Telugu culture fields
    @ColumnInfo(defaultValue = "")
    val deity: String? = null,
    @ColumnInfo(defaultValue = "")
    val deityTelugu: String? = null,
    @ColumnInfo(defaultValue = "")
    val district: String? = null,
    @ColumnInfo(defaultValue = "")
    val templeGroup: String? = null, // shakti_peethalu, pancha_arama, jyotirlinga, pancha_kshetra
    @ColumnInfo(defaultValue = "")
    val specialPoojasTelugu: String? = null,
    @ColumnInfo(defaultValue = "")
    val prasadamTelugu: String? = null,
)
