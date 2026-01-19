package com.example.data.mapper

import com.example.domain.error.DomainError
import java.io.IOException

internal fun Throwable.toDomainException(): DomainError = when (this) {
    is IOException -> DomainError.FileNotFoundError(this.message ?: "File not found")
    else -> DomainError.UnknownError(this.message ?: "Unknown error")
}