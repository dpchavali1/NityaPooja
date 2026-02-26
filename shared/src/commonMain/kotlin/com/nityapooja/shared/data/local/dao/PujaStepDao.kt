package com.nityapooja.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.shared.data.local.entity.PujaStepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PujaStepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PujaStepEntity>)

    @Query("SELECT * FROM puja_steps WHERE pujaType = :pujaType AND tier = :tier ORDER BY stepNumber")
    fun getSteps(pujaType: String, tier: String): Flow<List<PujaStepEntity>>

    @Query("SELECT DISTINCT pujaType FROM puja_steps")
    fun getAllPujaTypes(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM puja_steps")
    suspend fun getCount(): Int

    @Query("DELETE FROM puja_steps")
    suspend fun deleteAll()
}
