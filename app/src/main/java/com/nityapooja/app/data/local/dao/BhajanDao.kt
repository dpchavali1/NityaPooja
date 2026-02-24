package com.nityapooja.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.app.data.local.entity.BhajanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BhajanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bhajans: List<BhajanEntity>)

    @Query("SELECT * FROM bhajans ORDER BY id")
    fun getAllBhajans(): Flow<List<BhajanEntity>>

    @Query("SELECT * FROM bhajans WHERE id = :id")
    fun getBhajanById(id: Int): Flow<BhajanEntity?>

    @Query("SELECT * FROM bhajans WHERE deityId = :deityId")
    fun getBhajansByDeity(deityId: Int): Flow<List<BhajanEntity>>

    @Query("SELECT * FROM bhajans WHERE title LIKE '%' || :query || '%' OR titleTelugu LIKE '%' || :query || '%'")
    fun searchBhajans(query: String): Flow<List<BhajanEntity>>

    @Query("SELECT COUNT(*) FROM bhajans")
    suspend fun getCount(): Int
}
