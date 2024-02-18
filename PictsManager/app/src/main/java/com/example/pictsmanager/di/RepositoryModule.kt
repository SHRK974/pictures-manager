package com.example.pictsmanager.di

import com.example.pictsmanager.data.repository.AlbumRepositoryImpl
import com.example.pictsmanager.data.repository.ImageRepositoryImpl
import com.example.pictsmanager.data.repository.UserRepositoryImpl
import com.example.pictsmanager.domain.repository.AlbumRepository
import com.example.pictsmanager.domain.repository.ImageRepository
import com.example.pictsmanager.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAlbumRepository(
        albumRepositoryImpl: AlbumRepositoryImpl
    ): AlbumRepository
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
    @Binds
    @Singleton
    abstract fun bindImageRepository(
        imageRepositoryImpl: ImageRepositoryImpl
    ): ImageRepository
}