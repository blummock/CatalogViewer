package com.example.data.file.dto

import com.example.data.file.serializer.InstantSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
internal data class CatalogResponseDto(
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant,
    val items: List<BookDto>
)