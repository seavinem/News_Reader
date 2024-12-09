package com.example.newsreader.domain.repositories

import com.example.newsreader.domain.model.NewsModel
import com.example.newsreader.domain.model.NewsResult
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun fetchHtmlContent(url: String): Flow<NewsResult<String>>
    suspend fun getTopHeadlines(country: String) : Flow<NewsResult<List<NewsModel>>>
    suspend fun getEverything(query: String, from: String, to: String): Flow<NewsResult<List<NewsModel>>>
}