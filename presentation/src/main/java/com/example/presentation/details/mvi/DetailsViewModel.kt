package com.example.presentation.details.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.DataResult
import com.example.domain.repository.CatalogRepository
import com.example.presentation.common.model.toUi
import com.example.presentation.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DetailsViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle[AppRoute.Details.BOOK_ID])

    private val _effect = MutableSharedFlow<DetailsEffect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    private val _uiState = MutableStateFlow<DetailsState>(DetailsState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            when (val result = catalogRepository.getBookById(bookId)) {
                is DataResult.Error -> {
                    _uiState.value = DetailsState.Ready(book = null)
                    _effect.emit(DetailsEffect.OnShowError(result.error.message))
                }

                is DataResult.Success -> {
                    _uiState.value = DetailsState.Ready(book = result.data?.toUi())
                }
            }
        }
    }
}