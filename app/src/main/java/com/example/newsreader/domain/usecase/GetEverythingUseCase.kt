package com.example.newsreader.domain.usecase

import android.util.Log
import com.example.newsreader.data.model.NewsModel
import com.example.newsreader.domain.repository.NewsRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

private const val QUERY = "android"

class GetEverythingUseCase @Inject constructor(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(): Result<List<NewsModel>> {
        return kotlin.runCatching {
            val (today, yesterday) = getFormattedDates()
            val response = repository.getEverything(QUERY, yesterday, today)

            if (!response.isSuccessful || response.body().isNullOrEmpty()) {
                val errorMessage = response.errorBody()?.string() ?: "No news articles found"
                Log.w("GetTopHeadlinesUseCase", errorMessage)
                throw Exception(errorMessage)
            }

            response.body()!!.filter { it.title != "[Removed]" }
        }
    }

    private fun getFormattedDates(): Pair<String, String> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val today = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = dateFormat.format(calendar.time)

        return Pair(today, yesterday)
    }
}