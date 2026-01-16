package com.example.presentation.navigation

internal sealed interface AppRoute {
    val route: String

    data object Catalog : AppRoute {
        override val route = "catalog"
    }

    data object Details : AppRoute {
        const val BOOK_ID = "bookId"
        override val route = "details/{$BOOK_ID}"

        fun create(bookId: String) = "details/$bookId"
    }
}