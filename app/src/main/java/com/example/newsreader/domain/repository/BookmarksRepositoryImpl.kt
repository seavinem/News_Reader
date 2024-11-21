package com.example.newsreader.domain.repository

import android.content.Context
import com.example.newsreader.data.database.BookmarkEntity
import com.example.newsreader.data.database.BookmarksDatabase
import com.example.newsreader.data.model.NewsModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.UUID

class BookmarksRepositoryImpl(
    applicationContext: Context
) : BookmarksRepository {

    private val db = BookmarksDatabase.getInstance(applicationContext)

    override suspend fun isBookmarkExists(url: String): Boolean {
        return withContext(IO) {
            db.bookmarkDao().isBookmarkExists(url)
        }
    }

    override suspend fun getAllBookmarks(): List<NewsModel> {
        return withContext(IO) {
            db.bookmarkDao().getAll().map { mapToNewsModel(it) }
        }
    }

    override suspend fun saveBookmark(bookmark: BookmarkEntity) {
        withContext(IO) {
            db.bookmarkDao().addBookmark(bookmark)
        }
    }

    override suspend fun deleteBookmarkByUrl(url: String) {
        withContext(IO) {
            db.bookmarkDao().deleteBookmarkByUrl(url)
        }
    }

    private fun mapToNewsModel(bookmark: BookmarkEntity): NewsModel {
        return NewsModel(
            title = bookmark.title,
            description = bookmark.description,
            sourceName = bookmark.sourceName,
            author = bookmark.author,
            content = bookmark.content,
            url = bookmark.url,
            publishedAt = bookmark.publishedAt
        )
    }
}