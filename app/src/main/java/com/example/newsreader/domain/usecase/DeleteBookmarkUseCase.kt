package com.example.newsreader.domain.usecase

import com.example.newsreader.domain.repositories.BookmarksRepository
import javax.inject.Inject

class DeleteBookmarkUseCase @Inject constructor(
    private val repository: BookmarksRepository
) {
    suspend operator fun invoke(url: String) {
        repository.deleteBookmarkByUrl(url)
    }
}
