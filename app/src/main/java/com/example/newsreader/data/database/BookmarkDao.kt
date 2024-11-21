package com.example.newsreader.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarkentity")
    suspend fun getAll(): List<BookmarkEntity>

    @Query("SELECT COUNT(*) > 0 FROM bookmarkentity WHERE url = :url")
    suspend fun isBookmarkExists(url: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmarkEntity: BookmarkEntity)

    @Query("DELETE FROM bookmarkentity WHERE url = :url")
    suspend fun deleteBookmarkByUrl(url: String)
}