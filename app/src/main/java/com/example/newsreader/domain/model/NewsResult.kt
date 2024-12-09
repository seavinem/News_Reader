package com.example.newsreader.domain.model

sealed class NewsResult<T> {
    data class Loading<T>(val isLoading: Boolean) : NewsResult<T>()
    data class Success<T>(val content: T) : NewsResult<T>()
    data class Failure<T>(val errorMessage: String) : NewsResult<T>()
}