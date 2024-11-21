package com.example.newsreader.domain.repository

import com.example.newsreader.data.database.BookmarkEntity
import com.example.newsreader.data.model.NewsModel

interface BookmarksRepository {
    suspend fun isBookmarkExists(url: String): Boolean
    suspend fun getAllBookmarks(): List<NewsModel>
    suspend fun saveBookmark(bookmark: BookmarkEntity)
    suspend fun deleteBookmarkByUrl(url: String)
}