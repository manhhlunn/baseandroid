package com.example.baseandroid.resource.injection

import com.example.baseandroid.data.local.search.SearchRepository
import com.example.baseandroid.data.local.search.SearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface Modules {

    @Binds
    fun provideSearchRepository(searchRepositoryImpl: SearchRepositoryImpl): SearchRepository
}