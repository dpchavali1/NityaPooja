package com.nityapooja.shared.data.local.dao

import androidx.room.*
import com.nityapooja.shared.data.local.entity.FamilyDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDayDao {
    @Query("SELECT * FROM family_days WHERE isActive = 1 ORDER BY createdAt ASC")
    fun getAllFamilyDays(): Flow<List<FamilyDayEntity>>

    @Query("SELECT * FROM family_days WHERE id = :id")
    suspend fun getFamilyDayById(id: Long): FamilyDayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyDay(day: FamilyDayEntity): Long

    @Update
    suspend fun updateFamilyDay(day: FamilyDayEntity)

    @Query("DELETE FROM family_days WHERE id = :id")
    suspend fun deleteFamilyDayById(id: Long)
}
