package com.example.presentation.details.mvi

import com.example.presentation.common.model.BookUiItem

internal sealed interface DetailsState {
    data object Loading : DetailsState
    data class Ready(
        val book: BookUiItem?,
    ) : DetailsState
}