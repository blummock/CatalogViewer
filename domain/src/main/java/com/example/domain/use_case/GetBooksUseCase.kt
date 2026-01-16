package com.example.domain.use_case

import com.example.domain.entity.BookQuery
import com.example.domain.repository.CatalogRepository

class GetBooksUseCase(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(titleQuery: String) = catalogRepository
        .getBooks(BookQuery(title = titleQuery))
}