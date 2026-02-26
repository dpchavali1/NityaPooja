package com.nityapooja.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.shared.data.local.entity.KeertanaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KeertanaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keertanalu: List<KeertanaEntity>)

    @Query("SELECT * FROM keertanalu ORDER BY id")
    fun getAllKeertanalu(): Flow<List<KeertanaEntity>>

    @Query("SELECT * FROM keertanalu WHERE id = :id")
    fun getKeertanaById(id: Int): Flow<KeertanaEntity?>

    @Query("SELECT * FROM keertanalu WHERE deityId = :deityId")
    fun getKeertanaluByDeity(deityId: Int): Flow<List<KeertanaEntity>>

    @Query("SELECT * FROM keertanalu WHERE composer = :composer")
    fun getKeertanaluByComposer(composer: String): Flow<List<KeertanaEntity>>

    @Query("SELECT DISTINCT composer FROM keertanalu")
    fun getAllComposers(): Flow<List<String>>

    @Query("SELECT * FROM keertanalu WHERE title LIKE '%' || :query || '%' OR titleTelugu LIKE '%' || :query || '%' OR composer LIKE '%' || :query || '%'")
    fun searchKeertanalu(query: String): Flow<List<KeertanaEntity>>

    @Query("SELECT COUNT(*) FROM keertanalu")
    suspend fun getCount(): Int
}
