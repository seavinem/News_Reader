package com.example.newsreader.domain.usecase

import com.example.newsreader.domain.model.NewsResult
import com.example.newsreader.domain.repositories.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContentUseCase  @Inject constructor(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(url: String): Flow<NewsResult<String>> {
        return repository.fetchHtmlContent(url)
    }
}