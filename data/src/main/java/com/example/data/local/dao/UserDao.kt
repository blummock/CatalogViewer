package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun delete(id: String): Int

    @Transaction
    suspend fun toggleFavorite(id: String) {
        val rows = delete(id)
        if (rows == 0) {
            insert(FavoritesEntity(id))
        }
    }
}