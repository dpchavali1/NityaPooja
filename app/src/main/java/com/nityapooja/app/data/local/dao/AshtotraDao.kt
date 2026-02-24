package com.nityapooja.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.app.data.local.entity.AshtotraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AshtotraDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AshtotraEntity>)

    @Query("SELECT * FROM ashtotra ORDER BY id")
    fun getAll(): Flow<List<AshtotraEntity>>

    @Query("SELECT * FROM ashtotra WHERE id = :id")
    fun getById(id: Int): Flow<AshtotraEntity?>

    @Query("SELECT * FROM ashtotra WHERE deityId = :deityId")
    fun getByDeity(deityId: Int): Flow<List<AshtotraEntity>>

    @Query("SELECT COUNT(*) FROM ashtotra")
    suspend fun getCount(): Int
}
