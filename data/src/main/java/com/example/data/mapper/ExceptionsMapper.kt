package com.example.data.mapper

import com.example.domain.error.DomainError
import java.io.IOException

internal fun Throwable.toDomainException(): DomainError = when (this) {
    is IOException -> DomainError.FileNotFoundError
    else -> DomainError.UnknownError
}