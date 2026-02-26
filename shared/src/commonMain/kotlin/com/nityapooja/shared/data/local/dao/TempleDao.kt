package com.nityapooja.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.shared.data.local.entity.TempleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TempleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(temples: List<TempleEntity>)

    @Query("SELECT * FROM temples ORDER BY name")
    fun getAllTemples(): Flow<List<TempleEntity>>

    @Query("SELECT * FROM temples WHERE id = :id")
    fun getTempleById(id: Int): Flow<TempleEntity?>

    @Query("SELECT * FROM temples WHERE hasLiveDarshan = 1")
    fun getLiveDarshanTemples(): Flow<List<TempleEntity>>

    @Query("SELECT * FROM temples WHERE state = :state")
    fun getTemplesByState(state: String): Flow<List<TempleEntity>>

    @Query("SELECT * FROM temples WHERE name LIKE '%' || :query || '%' OR nameTelugu LIKE '%' || :query || '%'")
    fun searchTemples(query: String): Flow<List<TempleEntity>>

    @Query("SELECT COUNT(*) FROM temples")
    suspend fun getCount(): Int
}
