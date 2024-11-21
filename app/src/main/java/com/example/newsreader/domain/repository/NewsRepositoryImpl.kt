package com.example.newsreader.domain.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.newsreader.data.api.Article
import com.example.newsreader.data.api.NewsApi
import com.example.newsreader.data.api.NewsResponse
import com.example.newsreader.data.model.NewsModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.jsoup.Jsoup
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApi,
    @ApplicationContext private val context: Context
) : NewsRepository {

    override suspend fun getTopHeadlines(country: String): Response<List<NewsModel>> {
        if (!isInternetAvailable()) {
            Log.e("NewsRepository", "No internet connection available")
            return Response.error(
                503, // Service Unavailable
                "No internet connection".toResponseBody(null)
            )
        }

        val response = api.getTopHeadlines(country)

        if (!response.isSuccessful) {
            Log.e("NewsRepository", "API call failed with code: ${response.code()}")
            return Response.error(
                response.code(),
                response.errorBody() ?: "Unknown error".toResponseBody(null)
            )
        }

        val newsResponse = response.body()
        if (newsResponse?.status != "ok") {
            Log.e("NewsRepository", "API returned status: ${newsResponse?.status}")
            return Response.error(
                500,
                "Invalid API response status: ${newsResponse?.status}".toResponseBody(null)
            )
        }

        val newsList = newsResponse.articles.map { mapToNewsModel(it) }
        return Response.success(newsList)
    }

    override suspend fun getEverything(query: String, from: String, to: String): Response<List<NewsModel>> {
        if (!isInternetAvailable()) {
            Log.e("NewsRepository", "No internet connection available")
            return Response.error(
                503,
                "No internet connection".toResponseBody(null)
            )
        }

        val response = api.getEverything(query, from, to)

        if (!response.isSuccessful) {
            Log.e("NewsRepository", "API call failed with code: ${response.code()}")
            return Response.error(
                response.code(),
                response.errorBody() ?: "Unknown error".toResponseBody(null)
            )
        }

        val newsResponse = response.body()
        if (newsResponse?.status != "ok") {
            Log.e("NewsRepository", "API returned status: ${newsResponse?.status}")
            return Response.error(
                500,
                "Invalid API response status: ${newsResponse?.status}".toResponseBody(null)
            )
        }

        val newsList = newsResponse.articles.map { mapToNewsModel(it) }
        return Response.success(newsList)
    }

    private fun mapToNewsModel(article: Article): NewsModel {
        return NewsModel(
            title = article.title,
            description = article.description ?: "No Description provided.",
            sourceName = article.source.name,
            author = article.author,
            content = article.content ?: "No Content provided.",
            url = article.url,
            publishedAt = article.publishedAt.substring(0, 10)
        )
    }

    override suspend fun fetchHtmlContent(url: String): String {
        if (!isInternetAvailable()) {
            Log.e("NewsRepository", "No internet connection available for fetching HTML")
            return ""
        }

        return withContext(Dispatchers.IO) {
            try {
                val document = Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                    .followRedirects(false)
                    .get()
                val bodyText = document.body().text()
                Log.d("NewsRepository", "Loaded content from URL: $url")

                bodyText
            } catch (e: Exception) {
                Log.e("NewsRepository", "Failed to load content from URL: $url", e)
                ""
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}