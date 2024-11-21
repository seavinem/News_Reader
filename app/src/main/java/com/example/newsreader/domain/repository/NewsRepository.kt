package com.example.newsreader.domain.repository

import com.example.newsreader.data.model.NewsModel
import retrofit2.Response

interface NewsRepository {
    suspend fun fetchHtmlContent(url: String): String
    suspend fun getTopHeadlines(country: String) : Response<List<NewsModel>>
    suspend fun getEverything(query: String, from: String, to: String): Response<List<NewsModel>>
}