package com.nityapooja.shared.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vratas")
data class VrataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val nameTelugu: String,
    val category: String, // tithi_based, vaaram_based, masa_based
    @ColumnInfo(defaultValue = "-1")
    val tithiTrigger: Int = -1, // tithi index (0-29), -1 = not applicable
    @ColumnInfo(defaultValue = "-1")
    val vaaramTrigger: Int = -1, // 1=Sun..7=Sat, -1 = not applicable
    @ColumnInfo(defaultValue = "both")
    val paksham: String = "both", // shukla, krishna, both
    val frequency: String, // monthly, weekly, yearly
    val description: String? = null,
    val descriptionTelugu: String? = null,
    val fastingRules: String? = null,
    val fastingRulesTelugu: String? = null,
    val specialFoods: String? = null,
    val specialFoodsTelugu: String? = null,
    val associatedDeity: String? = null,
    val associatedDeityTelugu: String? = null,
    val mantras: String? = null,
    val mantrasTelugu: String? = null,
)
