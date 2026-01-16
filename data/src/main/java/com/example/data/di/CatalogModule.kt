package com.example.data.di

import com.example.data.repository.CatalogRepositoryImpl
import com.example.domain.repository.CatalogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface CatalogModule {

    @Binds
    fun bind(impl: CatalogRepositoryImpl): CatalogRepository
}