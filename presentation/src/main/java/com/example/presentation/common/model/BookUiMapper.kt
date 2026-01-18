package com.example.presentation.common.model


import com.example.domain.entity.Book
import java.text.DecimalFormat


private object DefaultFormat {
    val formater: DecimalFormat = DecimalFormat("#,##0.00")
}

internal fun Book.toUi() = BookUiItem(
    id = id,
    title = title,
    category = category.name,
    price = DefaultFormat.formater.format(price),
    rating = rating.toString(),
    isFavorite = isFavorite,
)