package com.example.data.mapper

import com.example.domain.exception.CommonError
import java.io.IOException

internal fun Throwable.toDomainException(): CommonError = when (this) {
    is IOException -> CommonError.FileNotFoundError
    else -> CommonError.UnknownError(this)
}