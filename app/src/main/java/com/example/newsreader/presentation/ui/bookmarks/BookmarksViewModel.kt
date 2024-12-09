package com.example.newsreader.presentation.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsreader.domain.model.NewsModel
import com.example.newsreader.domain.usecase.GetBookmarksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val getBookmarksUseCase: GetBookmarksUseCase
) : ViewModel() {

    private val _bookmarks = MutableStateFlow<List<NewsModel>>(emptyList())
    val bookmarks: StateFlow<List<NewsModel>> = _bookmarks

    init {
        fetchBookmarks()
    }

    private fun fetchBookmarks() {
        viewModelScope.launch {
            getBookmarksUseCase()
                .collect {  bookmarks ->
                    _bookmarks.value = bookmarks
                }
        }
    }

    fun onRefresh() {
        fetchBookmarks()
    }
}
