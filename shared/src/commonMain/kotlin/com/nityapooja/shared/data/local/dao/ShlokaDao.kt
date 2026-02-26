package com.nityapooja.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.shared.data.local.entity.ShlokaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShlokaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shlokas: List<ShlokaEntity>)

    @Query("SELECT * FROM shlokas WHERE dayOfYear = :dayOfYear LIMIT 1")
    fun getShlokaForDay(dayOfYear: Int): Flow<ShlokaEntity?>

    @Query("SELECT * FROM shlokas ORDER BY RANDOM() LIMIT 1")
    fun getRandomShloka(): Flow<ShlokaEntity?>

    @Query("SELECT COUNT(*) FROM shlokas")
    suspend fun getCount(): Int

    @Query("SELECT * FROM shlokas WHERE dayOfYear = :dayOfYear LIMIT 1")
    suspend fun getShlokaForDaySync(dayOfYear: Int): ShlokaEntity?
}
