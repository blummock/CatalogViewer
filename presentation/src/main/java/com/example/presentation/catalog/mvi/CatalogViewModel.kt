package com.example.presentation.catalog.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.DataResult
import com.example.domain.use_case.GetBooksUseCase
import com.example.domain.use_case.UpdateFavoriteUseCase
import com.example.presentation.common.model.BookUiItem
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
internal class CatalogViewModel @Inject constructor(
    val getBooksUseCase: GetBooksUseCase,
    val updateFavoriteUseCase: Lazy<UpdateFavoriteUseCase>,
) : ViewModel() {

    private val _effect = MutableSharedFlow<CatalogEffect>()
    val effect = _effect.asSharedFlow()

    private val _uiState = MutableStateFlow(CatalogState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            uiState.map { it.query }
                .distinctUntilChanged()
                .debounce(500)
                .collectLatest { query ->
                    when (val result = getBooksUseCase(query)) {
                        is DataResult.Error -> {
                            _uiState.update { it.setBooks(emptyList()) }
                            _effect.emit(CatalogEffect.OnShowError(result.error.message))
                        }

                        is DataResult.Success -> {
                            result.data.collect { books ->
                                _uiState.update { it.setBooks(books) }
                            }
                        }
                    }
                }
        }
    }

    fun onAction(action: CatalogAction) {
        when (action) {
            is CatalogAction.EditQuery -> editQuery(action.text)
            is CatalogAction.GoToBookView -> goToBookView(action.bookId)
            is CatalogAction.OnFavorite -> onFavorite(action.book)
        }
    }

    private fun onFavorite(book: BookUiItem) {
        viewModelScope.launch {
            updateFavoriteUseCase.get().invoke(
                id = book.id,
                isFavorite = !book.isFavorite
            ).let {
                if (it is DataResult.Error) {
                    _effect.emit(CatalogEffect.OnShowError(it.error.message))
                }
            }
        }
    }

    private fun editQuery(query: String) {
        _uiState.update { it.setQuery(query) }
    }

    private fun goToBookView(bookId: String) {
        viewModelScope.launch {
            _effect.emit(CatalogEffect.OnGoToBookView(bookId))
        }
    }
}