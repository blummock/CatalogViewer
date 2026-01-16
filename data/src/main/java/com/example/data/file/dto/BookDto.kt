package com.example.data.file.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class BookDto(
    val id: String,
    val title: String,
    val category: String,
    val price: Double,
    val rating: Double
)