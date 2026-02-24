package com.nityapooja.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.app.data.local.entity.ChalisaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChalisaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ChalisaEntity>)

    @Query("SELECT * FROM chalisas ORDER BY id")
    fun getAll(): Flow<List<ChalisaEntity>>

    @Query("SELECT * FROM chalisas WHERE id = :id")
    fun getById(id: Int): Flow<ChalisaEntity?>

    @Query("SELECT * FROM chalisas WHERE deityId = :deityId")
    fun getByDeityId(deityId: Int): Flow<List<ChalisaEntity>>

    @Query("SELECT * FROM chalisas WHERE title LIKE '%' || :query || '%' OR titleTelugu LIKE '%' || :query || '%'")
    fun searchChalisas(query: String): Flow<List<ChalisaEntity>>

    @Query("SELECT COUNT(*) FROM chalisas")
    suspend fun getCount(): Int
}
