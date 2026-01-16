package com.example.domain.entity

import java.math.BigDecimal

data class Book(
    val id: String,
    val title: String,
    val category: Category,
    val price: BigDecimal,
    val rating: Float,
    val isFavorite: Boolean,
)