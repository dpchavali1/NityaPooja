package com.nityapooja.shared.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chalisas",
    foreignKeys = [ForeignKey(
        entity = DeityEntity::class,
        parentColumns = ["id"],
        childColumns = ["deityId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("deityId")]
)
data class ChalisaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deityId: Int,
    val title: String,
    val titleTelugu: String,
    val doha: String? = null,
    val dohaTelugu: String? = null,
    val chaupai: String? = null,
    val chaupaiTelugu: String? = null,
    val audioUrl: String? = null,
    val duration: Int = 0,
    val audioSource: String? = null,
    val verseCount: Int = 40,
    val youtubeUrl: String? = null,
)
