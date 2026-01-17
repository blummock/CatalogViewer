package com.example.presentation.catalog.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.presentation.catalog.mvi.CatalogAction
import com.example.presentation.catalog.mvi.CatalogEffect
import com.example.presentation.catalog.mvi.CatalogViewModel
import com.example.presentation.R
import com.example.presentation.catalog.mvi.BooksState
import com.example.presentation.catalog.mvi.CatalogState
import com.example.presentation.common.model.BookUiItem
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun CatalogScreen(
    viewModel: CatalogViewModel,
    goToDetails: (bookId: String) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CatalogEffect.OnShowError -> {
                    snackbarHostState.showSnackbar(effect.msg)
                }

                is CatalogEffect.OnGoToBookView -> {
                    goToDetails(effect.bookId)
                }
            }
        }
    }
    CatalogScreenContent(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
private fun CatalogScreenContent(
    state: CatalogState,
    onAction: (CatalogAction) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        modifier = Modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            SearchBar(
                query = state.query,
                onQueryChange = { onAction(CatalogAction.EditQuery(it)) },
                enabled = state.booksState !is BooksState.Empty,
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                when (val booksState = state.booksState) {
                    BooksState.NotFound -> {
                        Text(text = "Books not found")
                    }

                    BooksState.Empty -> {
                        Text(text = "We have no books")
                    }

                    BooksState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is BooksState.Ready -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(10.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),

                            ) {
                            items(
                                items = booksState.books,
                                key = { it.id },
                            ) { book ->
                                BookItem(
                                    book = book,
                                    onClick = {
                                        onAction(CatalogAction.GoToBookView(bookId = book.id))
                                    },
                                    onFavorite = {
                                        onAction(CatalogAction.OnFavorite(bookId = book.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    enabled: Boolean,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        placeholder = { Text(stringResource(R.string.search)) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = stringResource(R.string.search)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear)
                    )
                }
            }
        },
        singleLine = true,
    )
}

@Composable
private fun BookItem(
    book: BookUiItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .drawBehind {
                drawRoundRect(
                    color = Color.LightGray,
                    cornerRadius = CornerRadius(5.dp.toPx())
                )
            }
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = book.title)
            Text(text = book.category)
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(stringResource(R.string.rating, book.rating))
            Text(stringResource(R.string.price, book.price))
        }
        IconButton(onClick = onFavorite) {
            Icon(
                imageVector = if (book.isFavorite) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                contentDescription = "Favorite"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogScreenPreview() {
    MaterialTheme {
        CatalogScreenContent(
            state = CatalogState(
                booksState = BooksState.Ready(
                    books = persistentListOf(
                        BookUiItem(
                            id = "bk_001",
                            title = "The Blue Fox",
                            category = "Fiction",
                            price = "12.99",
                            rating = "4.4",
                            isFavorite = true,
                        ),
                        BookUiItem(
                            id = "bk_002",
                            title = "Data Sketches",
                            category = "Non-Fiction",
                            price = "32.00",
                            rating = "4.8",
                            isFavorite = false,
                        )
                    )
                ),
            ),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}