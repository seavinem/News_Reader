package com.example.newsreader.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.newsreader.R
import com.example.newsreader.data.api.Article
import com.example.newsreader.data.api.NewsApi
import com.example.newsreader.domain.model.NewsModel
import com.example.newsreader.domain.model.NewsResult.*
import com.example.newsreader.domain.repositories.NewsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.jsoup.Jsoup
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApi,
    @ApplicationContext private val context: Context
) : NewsRepository {

    override suspend fun getTopHeadlines(country: String) = flow {
            if (isInternetAvailable()) {
                val response = api.getTopHeadlines(country)
                if (response.isSuccessful && response.body() != null) {
                    val newsList = response.body()!!.articles.map { mapToNewsModel(it) }

                    val newsResponse = response.body()
                    if (newsResponse?.status != "ok") {
                        emit(Failure(context.getString(R.string.invalid_api_response_status)))
                        return@flow
                    }

                    emit(Success(newsList))
                }
                else {
                    emit(Failure("Failed to fetch top headlines: ${response.code()}"))
                }
            }
            else {
                emit(Failure(context.getString(R.string.no_internet_connection)))
            }
        }.catch { error ->
            emit(Failure("Error $error"))
        }.flowOn(IO)

    override suspend fun getEverything(query: String, from: String, to: String) = flow {
            if (isInternetAvailable()) {
                val response = api.getEverything(query, from, to)
                if (response.isSuccessful && response.body() != null) {
                    val newsList = response.body()!!.articles.map { mapToNewsModel(it) }

                    val newsResponse = response.body()
                    if (newsResponse?.status != "ok") {
                        emit(Failure(context.getString(R.string.invalid_api_response_status)))
                        return@flow
                    }

                    emit(Success(newsList))
                }
                else {
                    emit(Failure("Failed to fetch news: ${response.code()}"))
                }
            }
            else {
                emit(Failure(context.getString(R.string.no_internet_connection)))
            }
        }.catch { error ->
            emit(Failure("Error $error"))
        }.flowOn(IO)

    private fun mapToNewsModel(article: Article): NewsModel {
        return NewsModel(
            title = article.title,
            description = article.description ?: "No Description provided.",
            sourceName = article.source.name,
            author = article.author ?: "Unknown author",
            content = article.content ?: "No Content provided.",
            url = article.url,
            publishedAt = article.publishedAt.take(10)
        )
    }

    override suspend fun fetchHtmlContent(url: String) = flow {
            if (!isInternetAvailable()) {
                emit(Failure(context.getString(R.string.no_internet_connection)))
                return@flow
            }

            try {
                val document = Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                    .followRedirects(false)
                    .get()

                val bodyText = document.body().text()
                emit(Success(bodyText))
            }
            catch (error: Exception) {
                emit(Failure(error.message ?: "Unknown error"))
            }
        }.flowOn(IO)

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}