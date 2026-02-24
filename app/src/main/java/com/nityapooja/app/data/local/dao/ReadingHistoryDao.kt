package com.nityapooja.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.app.data.local.entity.ReadingHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingHistoryDao {
    @Query("SELECT * FROM reading_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentHistory(limit: Int = 20): Flow<List<ReadingHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ReadingHistoryEntity)

    @Query("DELETE FROM reading_history WHERE contentType = :type AND contentId = :contentId")
    suspend fun deleteByContent(type: String, contentId: Int)

    @Query("DELETE FROM reading_history")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM reading_history")
    suspend fun getCount(): Int
}
