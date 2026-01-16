package com.example.presentation.details.mvi

internal sealed interface DetailsEffect {
    data class OnShowError(
        val msg: String,
    ) : DetailsEffect
}