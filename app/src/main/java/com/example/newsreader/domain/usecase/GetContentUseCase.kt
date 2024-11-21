package com.example.newsreader.domain.usecase

import android.util.Log
import com.example.newsreader.data.model.NewsModel
import com.example.newsreader.domain.repository.NewsRepository
import javax.inject.Inject

class GetContentUseCase  @Inject constructor(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(url: String): Result<String> {
        return kotlin.runCatching {
            val content = repository.fetchHtmlContent(url)

            if (content.isEmpty()) {
                throw Exception("Failed to load content from URL")
            }

            content
        }
    }
}