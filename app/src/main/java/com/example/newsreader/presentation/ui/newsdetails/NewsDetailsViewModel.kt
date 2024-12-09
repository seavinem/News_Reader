package com.example.newsreader.presentation.ui.newsdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsreader.data.database.BookmarkEntity
import com.example.newsreader.domain.model.NewsResult
import com.example.newsreader.domain.usecase.CheckBookmarkUseCase
import com.example.newsreader.domain.usecase.DeleteBookmarkUseCase
import com.example.newsreader.domain.usecase.GetContentUseCase
import com.example.newsreader.domain.usecase.SaveBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewsDetailsViewModel @Inject constructor(
    private val getContentUseCase: GetContentUseCase,
    private val saveBookmarkUseCase: SaveBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
    private val checkBookmarkUseCase: CheckBookmarkUseCase
) : ViewModel() {

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked

    private val _viewState = MutableStateFlow<NewsResult<String>>(NewsResult.Loading(true))
    val viewState: StateFlow<NewsResult<String>> get() = _viewState

    fun loadNewsContent(url: String) {

        viewModelScope.launch {
            getContentUseCase(url)
                .retry(3) { cause ->
                    delay(2000L)
                    cause is Exception
                }
                .catch {
                    _viewState.value = NewsResult.Failure("Connection error: Unable to load content.")
                }
                .collect { content ->
                    _viewState.value = content
                }

        }
    }

    fun toggleBookmark(
        title: String,
        description: String,
        sourceName: String,
        author: String,
        content: String,
        url: String,
        publishedAt: String
    ) {
        viewModelScope.launch {
            val bookmark = BookmarkEntity(
                uuid = UUID.randomUUID().toString(),
                title = title,
                description = description,
                sourceName = sourceName,
                author = author,
                content = content,
                url = url,
                publishedAt = publishedAt
            )

            if (_isBookmarked.value) {
                deleteBookmarkUseCase(url)
            } else {
                if (!checkBookmarkUseCase(title)) {
                    saveBookmarkUseCase(bookmark)
                }
            }
            _isBookmarked.value = !_isBookmarked.value
        }
    }

    fun checkIsBookmarked(url: String) {
        viewModelScope.launch {
            val isInBookmarks = checkBookmarkUseCase(url)
            _isBookmarked.value = isInBookmarks
        }
    }
}

