package com.example.presentation.catalog.mvi

internal interface CatalogAction {

    data class EditQuery(
        val text: String,
    ) : CatalogAction

    data class GoToBookView(
        val bookId: String,
    ) : CatalogAction

    data class OnFavorite(
        val bookId: String,
    ) : CatalogAction
}