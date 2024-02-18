package com.example.pictsmanager.di

import com.example.pictsmanager.data.remote.PicManagerApi
import com.example.pictsmanager.domain.util.DateAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
    }
    @Provides
    @Singleton
    fun providePicManagerApi(): PicManagerApi {
        return Retrofit.Builder()
            .baseUrl(System.getenv("BACK_API_URL") ?: "http://10.106.1.220:8081/api/")
            .addConverterFactory(MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(DateAdapter())
                    .build()
            ))
            .client(provideOkHttpClient())
            .build()
            .create()
    }
}