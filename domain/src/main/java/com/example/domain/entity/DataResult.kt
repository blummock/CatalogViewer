package com.example.domain.entity

import com.example.domain.error.DomainError

sealed interface DataResult<out T> {
    data class Success<T>(
        val data: T,
    ) : DataResult<T>

    data class Error<T>(
        val error: DomainError,
    ) : DataResult<T>
}