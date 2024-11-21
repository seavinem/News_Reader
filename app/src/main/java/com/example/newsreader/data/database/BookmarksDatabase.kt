package com.example.newsreader.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BookmarkEntity::class], version = 1)
abstract class BookmarksDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        private const val DATABASE_BOOKMARKS = "bookmarks_database"
        private var INSTANCE: BookmarksDatabase? = null

        fun getInstance(applicationContext: Context): BookmarksDatabase {
            if (INSTANCE == null) {
                synchronized(BookmarksDatabase::class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(applicationContext, BookmarksDatabase::class.java, DATABASE_BOOKMARKS).build()

                    }
                }
            }
            return INSTANCE!!
        }
    }
}
