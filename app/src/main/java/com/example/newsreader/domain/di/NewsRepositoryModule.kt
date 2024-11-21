package com.example.newsreader.domain.di

import com.example.newsreader.domain.repository.NewsRepository
import com.example.newsreader.domain.repository.NewsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NewsRepositoryModule {

    @Binds
    @Singleton
    fun bindNewsRepository(newsRepositoryImpl: NewsRepositoryImpl): NewsRepository
}
