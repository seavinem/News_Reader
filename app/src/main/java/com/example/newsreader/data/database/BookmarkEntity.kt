package com.example.newsreader.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookmarkEntity(
    @PrimaryKey val uuid: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "source") val sourceName: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "published_at") val publishedAt: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "url") val url: String,
)
