package com.example.data.file.source

import android.content.Context
import com.example.data.file.dto.CatalogResponseDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
internal class JsonDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Named("default") private val json: Json,
) {

    fun getCatalog(): CatalogResponseDto {
        val jsonString = context.assets.open(CATALOG_FILE)
            .bufferedReader()
            .use { it.readText() }

        return json.decodeFromString(jsonString)
    }

    private companion object Companion {
        const val CATALOG_FILE = "catalog.json"
    }
}