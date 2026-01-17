package com.example.domain.entity

import com.example.domain.error.CommonError

sealed interface DataResult<out T> {
    data class Success<T>(
        val data: T,
    ) : DataResult<T>

    data class Error<T>(
        val error: CommonError,
    ) : DataResult<T>
}