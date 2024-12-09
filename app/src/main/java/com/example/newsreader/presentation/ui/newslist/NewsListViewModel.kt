package com.example.newsreader.presentation.ui.newslist


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsreader.domain.model.NewsModel
import com.example.newsreader.domain.model.NewsResult
import com.example.newsreader.domain.usecase.GetEverythingUseCase
import com.example.newsreader.domain.usecase.GetTopHeadlinesUseCase
import com.example.newsreader.presentation.ui.utils.NewsType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val getEverythingUseCase: GetEverythingUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow<NewsResult<List<NewsModel>>>(NewsResult.Loading(true))
    val viewState: StateFlow<NewsResult<List<NewsModel>>> get() = _viewState

    private var _currentTab = MutableStateFlow(NewsType.TopHeadlines)
    val currentTab: StateFlow<NewsType> get() = _currentTab
    init {
        fetchNews()
    }

    fun setNewsType(type: NewsType) {
        _currentTab.value = type
        fetchNews()
    }

    private fun fetchNews() {
        viewModelScope.launch {
            val flow = when (_currentTab.value) {
                NewsType.TopHeadlines -> getTopHeadlinesUseCase()
                NewsType.Everything -> getEverythingUseCase()
            }

            flow
                .retry(retries = 3) { cause ->
                    delay(2000L)
                    cause is IOException
                }
                .catch { exception ->
                    _viewState.value = NewsResult.Failure("Failed to fetch news: ${exception.message}")
                }
                .collect { news ->
                    _viewState.value = news
                }
        }
    }
}