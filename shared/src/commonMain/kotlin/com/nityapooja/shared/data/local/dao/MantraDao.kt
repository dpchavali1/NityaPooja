package com.nityapooja.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.shared.data.local.entity.MantraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MantraDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mantras: List<MantraEntity>)

    @Query("SELECT * FROM mantras ORDER BY id")
    fun getAllMantras(): Flow<List<MantraEntity>>

    @Query("SELECT * FROM mantras WHERE id = :id")
    fun getMantraById(id: Int): Flow<MantraEntity?>

    @Query("SELECT * FROM mantras WHERE deityId = :deityId")
    fun getMantrasByDeity(deityId: Int): Flow<List<MantraEntity>>

    @Query("SELECT * FROM mantras WHERE category = :category")
    fun getMantrasByCategory(category: String): Flow<List<MantraEntity>>

    @Query("SELECT DISTINCT category FROM mantras")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT * FROM mantras WHERE title LIKE '%' || :query || '%' OR sanskrit LIKE '%' || :query || '%'")
    fun searchMantras(query: String): Flow<List<MantraEntity>>

    @Query("SELECT COUNT(*) FROM mantras")
    suspend fun getCount(): Int

    @Query("UPDATE mantras SET archiveOrgUrl = :url WHERE title = :title")
    suspend fun updateAudioUrlByTitle(title: String, url: String)
}
