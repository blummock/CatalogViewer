package com.example.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import javax.inject.Named


@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    @Singleton
    @Named("default")
    fun provideJson() = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Provides
    @Named("io")
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO
}