package com.nityapooja.app.data.local.dao

import androidx.room.*
import com.nityapooja.app.data.local.entity.JapaSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JapaSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: JapaSessionEntity)

    @Query("SELECT * FROM japa_sessions WHERE date = :date ORDER BY timestamp DESC")
    fun getSessionsByDate(date: String): Flow<List<JapaSessionEntity>>

    @Query("SELECT * FROM japa_sessions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 20): Flow<List<JapaSessionEntity>>

    @Query("SELECT COALESCE(SUM(count), 0) FROM japa_sessions")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(malasCompleted), 0) FROM japa_sessions")
    fun getTotalMalas(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT date) FROM japa_sessions")
    fun getActiveDaysCount(): Flow<Int>

    @Query("SELECT DISTINCT date FROM japa_sessions ORDER BY date DESC")
    fun getAllSessionDates(): Flow<List<String>>

    @Query("DELETE FROM japa_sessions")
    suspend fun clearAll()
}
