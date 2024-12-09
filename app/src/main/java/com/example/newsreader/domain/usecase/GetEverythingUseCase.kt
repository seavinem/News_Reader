package com.example.newsreader.domain.usecase

import com.example.newsreader.domain.model.NewsModel
import com.example.newsreader.domain.model.NewsResult
import com.example.newsreader.domain.repositories.NewsRepository
import com.example.newsreader.domain.utils.Config
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class GetEverythingUseCase @Inject constructor(
    private val repository: NewsRepository
) {
//    private val QUERY =

    suspend operator fun invoke(): Flow<NewsResult<List<NewsModel>>> {
        val (today, yesterday) = getFormattedDates()
        return repository.getEverything(Config.QUERY, yesterday, today)
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