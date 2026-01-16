package com.example.domain.use_case

import com.example.domain.repository.CatalogRepository

class UpdateFavoriteUseCase(
    private val catalogRepository: CatalogRepository
) {

    suspend operator fun invoke(id: String, isFavorite: Boolean) =
        catalogRepository.updateFavorite(id, isFavorite)
}