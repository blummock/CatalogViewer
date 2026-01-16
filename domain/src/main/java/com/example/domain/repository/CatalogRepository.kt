package com.example.domain.repository

import com.example.domain.entity.Book
import com.example.domain.entity.BookQuery
import com.example.domain.entity.DataResult
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {

    suspend fun getBooks(query: BookQuery): DataResult<Flow<List<Book>>>

    suspend fun getBookById(id: String): DataResult<Book?>

    suspend fun updateFavorite(id: String, isFavorite: Boolean): DataResult<Unit>
}