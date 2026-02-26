package com.nityapooja.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.shared.data.local.entity.RashiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RashiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RashiEntity>)

    @Query("SELECT * FROM rashis ORDER BY id")
    fun getAll(): Flow<List<RashiEntity>>

    @Query("SELECT * FROM rashis WHERE id = :id")
    fun getById(id: Int): Flow<RashiEntity?>

    @Query("SELECT COUNT(*) FROM rashis")
    suspend fun getCount(): Int
}
