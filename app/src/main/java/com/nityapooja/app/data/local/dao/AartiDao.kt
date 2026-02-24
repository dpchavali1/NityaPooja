package com.nityapooja.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.app.data.local.entity.AartiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AartiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(aartis: List<AartiEntity>)

    @Query("SELECT * FROM aartis ORDER BY id")
    fun getAllAartis(): Flow<List<AartiEntity>>

    @Query("SELECT * FROM aartis WHERE id = :id")
    fun getAartiById(id: Int): Flow<AartiEntity?>

    @Query("SELECT * FROM aartis WHERE deityId = :deityId")
    fun getAartisByDeity(deityId: Int): Flow<List<AartiEntity>>

    @Query("SELECT * FROM aartis WHERE title LIKE '%' || :query || '%' OR titleTelugu LIKE '%' || :query || '%'")
    fun searchAartis(query: String): Flow<List<AartiEntity>>

    @Query("SELECT COUNT(*) FROM aartis")
    suspend fun getCount(): Int

    @Query("UPDATE aartis SET archiveOrgUrl = :url WHERE title = :title")
    suspend fun updateAudioUrlByTitle(title: String, url: String)

    @Query("UPDATE aartis SET isCached = :cached, cachedFilePath = :path WHERE id = :id")
    suspend fun updateCacheStatus(id: Int, cached: Boolean, path: String?)
}
