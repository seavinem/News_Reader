package com.example.newsreader.data.repositories

import android.content.Context
import com.example.newsreader.data.database.BookmarkEntity
import com.example.newsreader.data.database.BookmarksDatabase
import com.example.newsreader.domain.model.NewsModel
import com.example.newsreader.domain.repositories.BookmarksRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BookmarksRepositoryImpl(
    applicationContext: Context
) : BookmarksRepository {

    private val db = BookmarksDatabase.getInstance(applicationContext)

    override suspend fun isBookmarkExists(url: String): Boolean {
        return withContext(IO) {
            db.bookmarkDao().isBookmarkExists(url)
        }
    }

    override suspend fun getAllBookmarks(): Flow<List<NewsModel>> {
        return db.bookmarkDao().getAll()
            .map { bookmarks -> bookmarks.map { mapToNewsModel(it) } }
            .flowOn(IO)
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