package com.example.data.repository

import com.example.data.file.source.JsonDataSource
import com.example.data.local.dao.FavoritesDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toDomainException
import com.example.domain.entity.Book
import com.example.domain.entity.BookQuery
import com.example.domain.entity.DataResult
import com.example.domain.repository.CatalogRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

internal class CatalogRepositoryImpl @Inject constructor(
    private val jsonDataSource: JsonDataSource,
    private val favoritesDao: FavoritesDao,
    @param:Named("io") private val dispatcher: CoroutineDispatcher,
) : CatalogRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getBooks(query: BookQuery): DataResult<Flow<List<Book>>> = withContext(dispatcher) {
        try {
            val books = with(jsonDataSource.getCatalog()) {
                if (query.title.isNotBlank()) {
                    items.filter {
                        it.title.contains(query.title, ignoreCase = true)
                    }
                } else {
                    items
                }
            }
            val domainBooks = favoritesDao.observeFavorites()
                .flowOn(dispatcher)
                .mapLatest { favorites ->
                    books.map {
                        it.toDomain(favorites.any { favorite ->
                            favorite.id == it.id
                        })
                    }
                }
            DataResult.Success(data = domainBooks)
        } catch (t: Throwable) {
            DataResult.Error(error = t.toDomainException())
        }
    }

    override suspend fun getBookById(id: String): DataResult<Book?> = withContext(dispatcher) {
        try {
            val item = jsonDataSource.getCatalog().items.firstOrNull { it.id == id }
            DataResult.Success(
                data = item?.toDomain(favoritesDao.selectById(id) != null)
            )

        } catch (t: Throwable) {
            DataResult.Error(error = t.toDomainException())
        }
    }

    override suspend fun toggleFavoriteBook(id: String): DataResult<Unit> = withContext(dispatcher)
    {
        try {
            favoritesDao.toggleFavorite(id)
            DataResult.Success(data = Unit)
        } catch (t: Throwable) {
            DataResult.Error(error = t.toDomainException())
        }
    }
}