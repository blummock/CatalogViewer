package com.example.domain.use_case

import com.example.domain.repository.CatalogRepository

class GetBookByIdUseCase(
    private val catalogRepository: CatalogRepository
) {

    suspend operator fun invoke(id: String) = catalogRepository.getBookById(id)
}