package com.example.presentation.catalog.mvi

import com.example.presentation.common.model.BookUiItem

internal interface CatalogAction {

    data class EditQuery(
        val text: String,
    ) : CatalogAction

    data class GoToBookView(
        val bookId: String,
    ) : CatalogAction

    data class OnFavorite(
        val book: BookUiItem,
    ) : CatalogAction
}