package com.example.newsreader.domain.usecase

import com.example.newsreader.data.model.NewsModel
import com.example.newsreader.domain.repository.BookmarksRepository
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(
    private val repository: BookmarksRepository
) {
    suspend operator fun invoke(): List<NewsModel> {
        return repository.getAllBookmarks()
    }
}
