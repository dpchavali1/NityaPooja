package com.nityapooja.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ashtotra",
    foreignKeys = [ForeignKey(
        entity = DeityEntity::class,
        parentColumns = ["id"],
        childColumns = ["deityId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("deityId")]
)
data class AshtotraEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deityId: Int,
    val title: String,
    val titleTelugu: String,
    val names: String? = null,
    val namesTelugu: String? = null,
    val audioUrl: String? = null,
    val duration: Int = 0,
    val archiveOrgUrl: String? = null,
    val localAssetName: String? = null,
    val audioSource: String? = null,
    val isCached: Boolean = false,
    val cachedFilePath: String? = null,
    val youtubeUrl: String? = null,
)
