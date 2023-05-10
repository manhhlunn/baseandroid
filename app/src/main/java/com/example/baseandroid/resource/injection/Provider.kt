package com.example.baseandroid.resource.injection

import android.content.Context
import androidx.paging.PagingConfig
import androidx.room.Room
import com.example.baseandroid.BuildConfig
import com.example.baseandroid.application.base.BasePagingSource
import com.example.baseandroid.data.local.AppDatabase
import com.example.baseandroid.data.local.DataStoreManager
import com.example.baseandroid.data.local.search.SearchHistoryDao
import com.example.baseandroid.data.network.ApiService
import com.example.baseandroid.data.network.ApiServiceWithoutToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Provider {

    @Provides
    fun provideRetrofit(
        @AuthInterceptorClient okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): ApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_ENDPOINT)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    fun provideRetrofitNoAuth(
        @WithoutAuthInterceptorClient okHttpClient: OkHttpClient,
        retrofitBuilder: Retrofit.Builder
    ): ApiServiceWithoutToken = retrofitBuilder
        .client(okHttpClient)
        .build()
        .create(ApiServiceWithoutToken::class.java)

    @Provides
    fun provideRetrofitBuilder(gsonConverterFactory: GsonConverterFactory): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_ENDPOINT)
            .addConverterFactory(gsonConverterFactory)

    @Provides
    fun provideAppDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, BuildConfig.APPLICATION_ID
        ).build()
    }

    @Provides
    fun provideHistoryDao(appDatabase: AppDatabase): SearchHistoryDao {
        return appDatabase.userDao()
    }

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .generateNonExecutableJson()
            .create()
    }

    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun provideUserDataStorePreferences(
        @ApplicationContext applicationContext: Context,
        gson: Gson
    ): DataStoreManager {
        return DataStoreManager(applicationContext, gson)
    }

    @Provides
    @Singleton
    fun providePagingConfig(): PagingConfig {
        return PagingConfig(
            pageSize = BasePagingSource.ITEM_PER_PAGE,
            enablePlaceholders = true
        )
    }
}