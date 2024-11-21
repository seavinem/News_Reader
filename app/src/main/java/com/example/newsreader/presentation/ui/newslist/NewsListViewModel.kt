package com.example.newsreader.presentation.ui.newslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsreader.data.model.NewsModel
import com.example.newsreader.domain.usecase.GetEverythingUseCase
import com.example.newsreader.domain.usecase.GetTopHeadlinesUseCase
import com.example.newsreader.presentation.ui.utils.NewsType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val getEverythingUseCase: GetEverythingUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<List<NewsModel>>()
    val viewState: LiveData<List<NewsModel>> get() = _viewState

    val progressBarStatus = MutableLiveData<Boolean>()

    private var _currentTab = MutableLiveData(NewsType.TopHeadlines)
    val currentTab: LiveData<NewsType> get() = _currentTab

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> get() = _errorState

    init {
        fetchNews()
    }

    fun setNewsType(type: NewsType) {
        _currentTab.value = type
        fetchNews()
    }

    private fun fetchNews() {
        progressBarStatus.value = true
        _errorState.value = null

        viewModelScope.launch {
            val result = retryWithDelay(
                maxAttempts = 3,
                delayMillis = 2000L
            ) {
                when (_currentTab.value) {
                    NewsType.TopHeadlines -> getTopHeadlinesUseCase()
                    NewsType.Everything -> getEverythingUseCase()
                    else -> Result.failure(Exception("Invalid news type"))
                }
            }

            result.onSuccess { news ->
                _viewState.postValue(news)
            }.onFailure { exception ->
                _errorState.postValue("Failed to fetch news: ${exception.message}")
            }

            progressBarStatus.postValue(false)
        }
    }

    fun clearError() {
        _errorState.value = null
    }

    private suspend fun <T> retryWithDelay(
        maxAttempts: Int,
        delayMillis: Long,
        block: suspend () -> Result<T>
    ) : Result<T> {
        repeat(maxAttempts - 1) {
            val result = block()
            if (result.isSuccess) return result
            delay(delayMillis)
        }
        return block()
    }
}