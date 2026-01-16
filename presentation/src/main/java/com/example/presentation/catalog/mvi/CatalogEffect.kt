package com.example.presentation.catalog.mvi

internal interface CatalogEffect {

    data class OnShowError(
        val msg: String,
    ) : CatalogEffect

    data class OnGoToBookView(
        val bookId: String,
    ) : CatalogEffect
}