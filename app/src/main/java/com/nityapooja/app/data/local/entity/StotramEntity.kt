package com.nityapooja.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stotrams",
    foreignKeys = [ForeignKey(
        entity = DeityEntity::class,
        parentColumns = ["id"],
        childColumns = ["deityId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("deityId")]
)
data class StotramEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deityId: Int,
    val title: String,
    val titleTelugu: String,
    val textSanskrit: String? = null,
    val textTelugu: String? = null,
    val textEnglish: String? = null,
    val audioUrl: String? = null,
    val duration: Int = 0,
    val verseCount: Int = 0,
    val archiveOrgUrl: String? = null,
    val localAssetName: String? = null,
    val audioSource: String? = null,
    val isCached: Boolean = false,
    val cachedFilePath: String? = null,
    val youtubeUrl: String? = null,
)
