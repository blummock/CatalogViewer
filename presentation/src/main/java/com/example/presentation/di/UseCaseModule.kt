package com.example.presentation.di

import com.example.domain.repository.CatalogRepository
import com.example.domain.use_case.GetBookByIdUseCase
import com.example.domain.use_case.GetBooksUseCase
import com.example.domain.use_case.UpdateFavoriteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal object UseCaseModule {

    @Provides
    fun provideGetUserUseCase(
        repository: CatalogRepository
    ): GetBooksUseCase {
        return GetBooksUseCase(repository)
    }

    @Provides
    fun provideGetBookByIdUseCase(
        repository: CatalogRepository
    ): GetBookByIdUseCase {
        return GetBookByIdUseCase(repository)
    }

    @Provides
    fun provideUpdateFavoriteUseCase(
        repository: CatalogRepository
    ): UpdateFavoriteUseCase {
        return UpdateFavoriteUseCase(repository)
    }
}