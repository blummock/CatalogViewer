package com.example.presentation.details.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.R
import com.example.presentation.details.mvi.DetailsEffect
import com.example.presentation.details.mvi.DetailsState
import com.example.presentation.details.mvi.DetailsViewModel

@Composable
internal fun DetailsScreen(viewModel: DetailsViewModel) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DetailsEffect.OnShowError -> {
                    snackbarHostState.showSnackbar(effect.msg)
                }
            }
        }
    }
    DetailsScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
private fun DetailsScreenContent(
    state: DetailsState,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        modifier = Modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                DetailsState.Loading -> {
                    CircularProgressIndicator()
                }

                is DetailsState.Ready -> {
                    val book = state.book
                    if (book == null) {
                        Text(text = "Book not found")
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.string_id, book.id)
                            )
                            Text(
                                text = stringResource(R.string.title, book.title)
                            )
                            Text(
                                text = stringResource(R.string.category, book.category),
                            )
                            Text(
                                text = stringResource(R.string.price, book.price),
                            )
                            Text(
                                text = stringResource(R.string.rating, book.rating)
                            )
                            Text(
                                text = stringResource(R.string.favorites, book.isFavorite)
                            )
                        }
                    }
                }
            }
        }
    }
}