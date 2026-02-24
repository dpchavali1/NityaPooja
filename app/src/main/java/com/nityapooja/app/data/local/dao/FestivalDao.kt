package com.nityapooja.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.app.data.local.entity.FestivalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FestivalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(festivals: List<FestivalEntity>)

    @Query("SELECT * FROM festivals ORDER BY date")
    fun getAllFestivals(): Flow<List<FestivalEntity>>

    @Query("SELECT * FROM festivals WHERE id = :id")
    fun getFestivalById(id: Int): Flow<FestivalEntity?>

    @Query("SELECT COUNT(*) FROM festivals")
    suspend fun getCount(): Int
}
