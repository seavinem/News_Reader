package com.example.newsreader.domain.usecase

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.newsreader.data.model.NewsModel
import com.example.newsreader.domain.repository.NewsRepository
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

private const val COUNTRY = "us"

class GetTopHeadlinesUseCase @Inject constructor(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(): Result<List<NewsModel>> {
        return kotlin.runCatching {
            val response = repository.getTopHeadlines(COUNTRY)

            if (!response.isSuccessful || response.body().isNullOrEmpty()) {
                val errorMessage = response.errorBody()?.string() ?: "No news articles found"
                Log.w("GetTopHeadlinesUseCase", errorMessage)
                throw Exception(errorMessage)
            }

            response.body()!!.filter { it.title != "[Removed]" }
        }
    }
}