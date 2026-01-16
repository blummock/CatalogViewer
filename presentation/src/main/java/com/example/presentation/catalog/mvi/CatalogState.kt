package com.example.presentation.catalog.mvi

import com.example.presentation.common.model.BookUiItem
import kotlinx.collections.immutable.ImmutableList

internal data class CatalogState(
    val query: String = "",
    val booksState: BooksState = BooksState.Loading,
)

internal sealed interface BooksState {

    data class Ready(
        val books: ImmutableList<BookUiItem>
    ) : BooksState

    data object Loading : BooksState

    data object Empty : BooksState

    data object NotFound : BooksState
}