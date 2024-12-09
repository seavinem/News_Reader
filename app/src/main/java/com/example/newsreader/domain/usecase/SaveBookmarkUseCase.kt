package com.example.newsreader.domain.usecase

import com.example.newsreader.data.database.BookmarkEntity
import com.example.newsreader.domain.repositories.BookmarksRepository
import javax.inject.Inject

class SaveBookmarkUseCase @Inject constructor(
    private val repository: BookmarksRepository
) {
    suspend operator fun invoke(bookmark: BookmarkEntity) {
        repository.saveBookmark(bookmark)
    }
}
