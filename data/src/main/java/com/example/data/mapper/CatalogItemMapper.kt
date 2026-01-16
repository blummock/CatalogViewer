package com.example.data.mapper

import com.example.data.file.dto.BookDto
import com.example.domain.entity.Book
import com.example.domain.entity.Category

internal fun BookDto.toDomain(isFavorite: Boolean): Book {
    return Book(
        id = id,
        title = title,
        category = category.toDomain(),
        price = price.toBigDecimal(),
        rating = rating.toFloat(),
        isFavorite = isFavorite,
    )
}

private fun String.toDomain(): Category =
    when (uppercase()) {
        "FICTION" -> Category.FICTION
        "NON-FICTION" -> Category.NON_FICTION
        "TECH" -> Category.TECH
        else -> Category.UNKNOWN
    }