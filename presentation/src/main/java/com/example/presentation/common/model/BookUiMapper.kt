package com.example.presentation.common.model

import android.icu.text.DecimalFormat
import com.example.domain.entity.Book


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