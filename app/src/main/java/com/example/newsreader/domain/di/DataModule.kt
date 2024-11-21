package com.example.newsreader.domain.di

import android.content.Context
import com.example.newsreader.domain.repository.BookmarksRepository
import com.example.newsreader.domain.repository.BookmarksRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(FragmentComponent::class, ViewModelComponent::class)
object DataModule {

    @Provides
    fun provideBookmarksRepository(@ApplicationContext applicationContext: Context): BookmarksRepository {
        return BookmarksRepositoryImpl(applicationContext)
    }
}