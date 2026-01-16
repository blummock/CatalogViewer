package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.FavoritesEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FavoritesDao {

    @Query("SELECT * FROM favorites")
    fun observeFavorites(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun selectById(id: String): FavoritesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoritesEntity)

    @Delete
    suspend fun delete(entity: FavoritesEntity)
}