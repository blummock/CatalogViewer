package com.example.data.file.api

import android.content.Context
import com.example.data.file.dto.CatalogResponseDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CatalogApi @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun getCatalog(): CatalogResponseDto {
        val jsonString = context.assets.open(CATALOG_FILE)
            .bufferedReader()
            .use { it.readText() }

        return json.decodeFromString(jsonString)
    }

    private companion object {
        const val CATALOG_FILE = "catalog.json"
    }
}