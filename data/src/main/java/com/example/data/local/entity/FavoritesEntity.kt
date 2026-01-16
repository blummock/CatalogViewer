package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
internal data class FavoritesEntity(
    @PrimaryKey val id: String,
)