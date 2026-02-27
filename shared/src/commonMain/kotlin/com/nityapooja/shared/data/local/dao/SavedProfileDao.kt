package com.nityapooja.shared.data.local.dao

import androidx.room.*
import com.nityapooja.shared.data.local.entity.SavedProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedProfileDao {
    @Query("SELECT * FROM saved_profiles ORDER BY updatedAt DESC")
    fun getAllProfiles(): Flow<List<SavedProfileEntity>>

    @Query("SELECT * FROM saved_profiles WHERE id = :id")
    suspend fun getProfileById(id: Long): SavedProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: SavedProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: SavedProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: SavedProfileEntity)

    @Query("SELECT COUNT(*) FROM saved_profiles")
    suspend fun getCount(): Int

    @Query("SELECT * FROM saved_profiles WHERE name = :name AND year = :year AND month = :month AND day = :day AND hour = :hour AND minute = :minute LIMIT 1")
    suspend fun findByNameAndBirth(name: String, year: Int, month: Int, day: Int, hour: Int, minute: Int): SavedProfileEntity?
}
