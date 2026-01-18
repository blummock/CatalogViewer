package com.example.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.FavoritesDao
import com.example.data.local.entity.FavoritesEntity

@Database(
    entities = [FavoritesEntity::class],
    exportSchema = false,
    version = 1
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
}