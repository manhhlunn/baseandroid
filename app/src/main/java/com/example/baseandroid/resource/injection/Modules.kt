package com.example.baseandroid.resource.injection

import com.example.gurume_go_android.data.SearchRepository
import com.example.gurume_go_android.data.SearchRepositoryImpl
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