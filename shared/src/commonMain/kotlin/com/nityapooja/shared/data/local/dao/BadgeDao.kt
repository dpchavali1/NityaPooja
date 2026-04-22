package com.nityapooja.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.shared.data.local.entity.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {

    @Query("SELECT * FROM badges ORDER BY unlockedAt ASC")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badges WHERE badgeType = :type LIMIT 1")
    suspend fun getBadge(type: String): BadgeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: BadgeEntity)

    @Query("UPDATE badges SET shownToUser = 1 WHERE badgeType = :type")
    suspend fun markShown(type: String)
}
