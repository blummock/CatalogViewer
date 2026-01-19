package com.example.domain.error

sealed interface DomainError {

    val message: String

    data object FileNotFoundError : DomainError {
        override val message: String = "File not found"
    }

    data object UnknownError : DomainError {
        override val message: String = "Unknown error"
    }
}