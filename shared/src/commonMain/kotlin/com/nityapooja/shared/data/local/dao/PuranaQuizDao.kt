package com.nityapooja.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.shared.data.local.entity.PuranaQuizEntity

@Dao
interface PuranaQuizDao {
    @Query("SELECT * FROM purana_quizzes ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuizzes(limit: Int = 5): List<PuranaQuizEntity>

    @Query("SELECT COUNT(*) FROM purana_quizzes")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quizzes: List<PuranaQuizEntity>)
}
