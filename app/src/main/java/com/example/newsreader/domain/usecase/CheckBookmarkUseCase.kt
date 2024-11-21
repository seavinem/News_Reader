package com.example.newsreader.domain.usecase

import com.example.newsreader.domain.repository.BookmarksRepository
import javax.inject.Inject

class CheckBookmarkUseCase @Inject constructor(
    private val repository: BookmarksRepository
) {
    suspend operator fun invoke(url: String): Boolean {
        return repository.isBookmarkExists(url)
    }
}
