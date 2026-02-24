package com.nityapooja.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nityapooja.app.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Delete
    suspend fun delete(bookmark: BookmarkEntity)

    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE contentType = :type")
    fun getBookmarksByType(type: String): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE contentType = :type AND contentId = :contentId)")
    fun isBookmarked(type: String, contentId: Int): Flow<Boolean>

    @Query("DELETE FROM bookmarks WHERE contentType = :type AND contentId = :contentId")
    suspend fun removeBookmark(type: String, contentId: Int)

    @Query("DELETE FROM bookmarks")
    suspend fun clearAll()
}
