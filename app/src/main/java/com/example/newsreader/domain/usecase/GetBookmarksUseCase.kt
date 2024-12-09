package com.example.newsreader.domain.usecase

import com.example.newsreader.domain.model.NewsModel
import com.example.newsreader.domain.repositories.BookmarksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(
    private val repository: BookmarksRepository
) {
    suspend operator fun invoke(): Flow<List<NewsModel>> {
        return repository.getAllBookmarks()
    }
}
