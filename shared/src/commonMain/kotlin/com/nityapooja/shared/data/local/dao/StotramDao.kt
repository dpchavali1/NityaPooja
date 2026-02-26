package com.nityapooja.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.shared.data.local.entity.StotramEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StotramDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stotrams: List<StotramEntity>)

    @Query("SELECT * FROM stotrams ORDER BY id")
    fun getAllStotrams(): Flow<List<StotramEntity>>

    @Query("SELECT * FROM stotrams WHERE id = :id")
    fun getStotramById(id: Int): Flow<StotramEntity?>

    @Query("SELECT * FROM stotrams WHERE deityId = :deityId")
    fun getStotramsByDeity(deityId: Int): Flow<List<StotramEntity>>

    @Query("SELECT * FROM stotrams WHERE title LIKE '%' || :query || '%' OR titleTelugu LIKE '%' || :query || '%'")
    fun searchStotrams(query: String): Flow<List<StotramEntity>>

    @Query("SELECT COUNT(*) FROM stotrams")
    suspend fun getCount(): Int

    @Query("UPDATE stotrams SET archiveOrgUrl = :url WHERE title = :title")
    suspend fun updateAudioUrlByTitle(title: String, url: String)
}
