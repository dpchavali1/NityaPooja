package com.nityapooja.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rashis")
data class RashiEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val nameTelugu: String,
    val symbol: String,
    val dateRange: String,
    val dateRangeTelugu: String,
    val element: String,
    val rulingPlanet: String,
    val rulingPlanetTelugu: String,
    val predictionSun: String? = null,
    val predictionSunTelugu: String? = null,
    val predictionMon: String? = null,
    val predictionMonTelugu: String? = null,
    val predictionTue: String? = null,
    val predictionTueTelugu: String? = null,
    val predictionWed: String? = null,
    val predictionWedTelugu: String? = null,
    val predictionThu: String? = null,
    val predictionThuTelugu: String? = null,
    val predictionFri: String? = null,
    val predictionFriTelugu: String? = null,
    val predictionSat: String? = null,
    val predictionSatTelugu: String? = null,
)
