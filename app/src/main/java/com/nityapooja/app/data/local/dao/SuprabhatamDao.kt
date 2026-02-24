package com.nityapooja.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.app.data.local.entity.SuprabhatamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SuprabhatamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SuprabhatamEntity>)

    @Query("SELECT * FROM suprabhatam ORDER BY id")
    fun getAll(): Flow<List<SuprabhatamEntity>>

    @Query("SELECT * FROM suprabhatam WHERE id = :id")
    fun getById(id: Int): Flow<SuprabhatamEntity?>

    @Query("SELECT * FROM suprabhatam WHERE deityId = :deityId")
    fun getByDeity(deityId: Int): Flow<List<SuprabhatamEntity>>

    @Query("SELECT COUNT(*) FROM suprabhatam")
    suspend fun getCount(): Int
}
