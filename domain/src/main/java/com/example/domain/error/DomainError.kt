package com.example.domain.error

sealed interface DomainError {

    val message: String

    data class FileNotFoundError(
        override val message: String
    ) : DomainError

    data class UnknownError(
        override val message: String
    ) : DomainError
}