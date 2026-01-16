package com.example.presentation.catalog.mvi

import com.example.domain.entity.Book
import com.example.presentation.common.model.toUi
import kotlinx.collections.immutable.toImmutableList

internal fun CatalogState.setBooks(books: List<Book>) = copy(
    booksState = when {
        books.isEmpty() && query.isNotBlank() -> BooksState.NotFound
        books.isEmpty() -> BooksState.Empty
        else -> BooksState.Ready(
            books = books.map { it.toUi() }.toImmutableList()
        )
    }
)

internal fun CatalogState.setQuery(text: String) = copy(
    query = text,
)