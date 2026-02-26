package com.nityapooja.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puja_steps")
data class PujaStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pujaType: String,
    val tier: String,
    val stepNumber: Int,
    val title: String,
    val titleTelugu: String,
    val instruction: String,
    val instructionTelugu: String,
    val itemsNeeded: String? = null,
    val itemsNeededTelugu: String? = null,
    val mantra: String? = null,
    val mantraTelugu: String? = null,
    val durationSeconds: Int = 60,
)
