package com.example.newsreader.presentation.ui.bookmarks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsreader.data.model.NewsModel
import com.example.newsreader.domain.usecase.GetBookmarksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val getBookmarksUseCase: GetBookmarksUseCase
) : ViewModel() {

    private val _bookmarks = MutableLiveData<List<NewsModel>>()
    val bookmarks: LiveData<List<NewsModel>> = _bookmarks
    val progressBarStatus = MutableLiveData<Boolean>()

    init {
        fetchBookmarks()
    }

    private fun fetchBookmarks() {
        progressBarStatus.value = true

        viewModelScope.launch {
            val allBookmarks = getBookmarksUseCase()
            _bookmarks.postValue(allBookmarks)
            progressBarStatus.value = false
        }
    }

    fun onRefresh() {
        fetchBookmarks()
    }
}
