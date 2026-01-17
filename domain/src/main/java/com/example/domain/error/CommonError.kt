package com.example.domain.error

sealed class CommonError(
    val message: String,
    open val cause: Throwable? = null,
) {

    object FileNotFoundError : CommonError("File not found")

    data class UnknownError(
        override val cause: Throwable,
    ) : CommonError(message = "Unknown error")
}