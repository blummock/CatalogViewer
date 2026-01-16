package com.example.presentation.common.model

internal data class BookUiItem(
    val id: String,
    val title: String,
    val category: String,
    val price: String,
    val rating: String,
    val isFavorite: Boolean,
)