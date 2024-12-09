package com.example.newsreader.domain.repositories

import com.example.newsreader.data.database.BookmarkEntity
import com.example.newsreader.domain.model.NewsModel
import kotlinx.coroutines.flow.Flow

interface BookmarksRepository {
    suspend fun isBookmarkExists(url: String): Boolean
    suspend fun getAllBookmarks(): Flow<List<NewsModel>>
    suspend fun saveBookmark(bookmark: BookmarkEntity)
    suspend fun deleteBookmarkByUrl(url: String)
}