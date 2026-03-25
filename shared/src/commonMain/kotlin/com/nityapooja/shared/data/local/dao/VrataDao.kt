package com.nityapooja.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.shared.data.local.entity.VrataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VrataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vratas: List<VrataEntity>)

    @Query("SELECT * FROM vratas ORDER BY name")
    fun getAllVratas(): Flow<List<VrataEntity>>

    @Query("SELECT * FROM vratas WHERE id = :id")
    fun getVrataById(id: Int): Flow<VrataEntity?>

    @Query("SELECT * FROM vratas WHERE tithiTrigger = :tithiIndex AND (paksham = :paksham OR paksham = 'both')")
    fun getVratasByTithi(tithiIndex: Int, paksham: String): Flow<List<VrataEntity>>

    @Query("SELECT * FROM vratas WHERE vaaramTrigger = :dayOfWeek")
    fun getVratasByDay(dayOfWeek: Int): Flow<List<VrataEntity>>

    @Query("SELECT * FROM vratas WHERE category = :category")
    fun getVratasByCategory(category: String): Flow<List<VrataEntity>>

    @Query("SELECT COUNT(*) FROM vratas")
    suspend fun getCount(): Int

    @Query("DELETE FROM vratas")
    suspend fun clearAll()
}
