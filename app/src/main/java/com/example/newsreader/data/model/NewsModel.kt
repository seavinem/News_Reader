package com.example.newsreader.data.model

data class NewsModel(
    val title: String,
    val description: String,
    val sourceName: String,
    val author: String?,
    val content: String,
    val url: String,
    val publishedAt: String,
)