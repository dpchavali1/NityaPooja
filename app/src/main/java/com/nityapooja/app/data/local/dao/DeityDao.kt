package com.nityapooja.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.app.data.local.entity.DeityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(deities: List<DeityEntity>)

    @Query("SELECT * FROM deities ORDER BY id")
    fun getAllDeities(): Flow<List<DeityEntity>>

    @Query("SELECT * FROM deities WHERE id = :id")
    fun getDeityById(id: Int): Flow<DeityEntity?>

    @Query("SELECT * FROM deities WHERE dayOfWeek = :day")
    fun getDeityByDay(day: String): Flow<List<DeityEntity>>

    @Query("SELECT COUNT(*) FROM deities")
    suspend fun getCount(): Int
}
