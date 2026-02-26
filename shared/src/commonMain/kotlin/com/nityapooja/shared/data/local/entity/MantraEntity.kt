package com.nityapooja.shared.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mantras",
    foreignKeys = [ForeignKey(
        entity = DeityEntity::class,
        parentColumns = ["id"],
        childColumns = ["deityId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("deityId")]
)
data class MantraEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deityId: Int,
    val title: String,
    val titleTelugu: String,
    val sanskrit: String? = null,
    val meaningTelugu: String? = null,
    val meaningEnglish: String? = null,
    val benefits: String? = null,
    val benefitsTelugu: String? = null,
    val audioUrl: String? = null,
    val category: String? = null,
    val recommendedCount: Int = 108,
    val archiveOrgUrl: String? = null,
    val localAssetName: String? = null,
    val audioSource: String? = null,
    val isCached: Boolean = false,
    val cachedFilePath: String? = null,
    val youtubeUrl: String? = null,
)
