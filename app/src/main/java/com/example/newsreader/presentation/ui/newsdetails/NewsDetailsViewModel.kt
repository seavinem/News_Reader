package com.example.newsreader.presentation.ui.newsdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsreader.data.database.BookmarkEntity
import com.example.newsreader.domain.usecase.CheckBookmarkUseCase
import com.example.newsreader.domain.usecase.DeleteBookmarkUseCase
import com.example.newsreader.domain.usecase.GetContentUseCase
import com.example.newsreader.domain.usecase.SaveBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewsDetailsViewModel @Inject constructor(
    private val getContentUseCase: GetContentUseCase,
    private val saveBookmarkUseCase: SaveBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
    private val checkBookmarkUseCase: CheckBookmarkUseCase
) : ViewModel() {

    private val _isBookmarked = MutableLiveData(false)
    val isBookmarked: LiveData<Boolean> = _isBookmarked

    private val _newsContent = MutableLiveData<String>()
    val newsContent: LiveData<String> get() = _newsContent

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> get() = _errorState

    fun loadNewsContent(url: String) {
        _isLoading.value = true
        _errorState.value = null

        viewModelScope.launch {
            val result = retryWithDelay(
                maxAttempts = 3,
                delayMillis = 2000L
            ) {
                try {
                    getContentUseCase(url)
                }
                catch (e: IOException) {
                    throw e
                }
            }

            result.onSuccess { content ->
                _newsContent.value = content
            }.onFailure { exception ->
                if (exception is IOException) {
                    _errorState.postValue("Connection error: Unable to load content.")
                }
                else {
                    _newsContent.value = ""
                }
            }
            _isLoading.value = false
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

            if (_isBookmarked.value == true) {
                deleteBookmarkUseCase(url)
            }
            else {
                if (!checkBookmarkUseCase(title)) {
                    saveBookmarkUseCase(bookmark)
                }
            }
            _isBookmarked.postValue(!_isBookmarked.value!!)
        }
    }

    fun checkIsBookmarked(url: String) {
        viewModelScope.launch {
            val isInBookmarks = checkBookmarkUseCase(url)
            _isBookmarked.postValue(isInBookmarks)
        }
    }

    fun clearError() {
        _errorState.value = null
    }

    suspend fun <T> retryWithDelay(
        maxAttempts: Int,
        delayMillis: Long,
        block: suspend () -> T
    ): T {
        repeat(maxAttempts - 1) { attempt ->
            try {
                return block()
            } catch (e: IOException) {
                if (attempt == maxAttempts - 1) throw e
                delay(delayMillis) // Задержка перед повторной попыткой
            }
        }
        return block() // Последняя попытка
    }

}

