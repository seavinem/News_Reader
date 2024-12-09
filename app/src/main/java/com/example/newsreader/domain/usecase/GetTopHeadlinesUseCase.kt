package com.example.newsreader.domain.usecase

import com.example.newsreader.domain.model.NewsModel
import com.example.newsreader.domain.model.NewsResult
import com.example.newsreader.domain.repositories.NewsRepository
import com.example.newsreader.domain.utils.Config
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

//private const val COUNTRY = Config.COUNTRY

class GetTopHeadlinesUseCase @Inject constructor(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(): Flow<NewsResult<List<NewsModel>>> {
        return repository.getTopHeadlines(Config.COUNTRY)
    }
}