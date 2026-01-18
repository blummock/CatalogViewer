package com.example.data.repository

import app.cash.turbine.test
import com.example.data.file.dto.BookDto
import com.example.data.file.dto.CatalogResponseDto
import com.example.data.file.source.JsonDataSource
import com.example.data.local.dao.FavoritesDao
import com.example.data.local.entity.FavoritesEntity
import com.example.domain.entity.BookQuery
import com.example.domain.entity.DataResult
import com.example.domain.error.CommonError
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.time.Instant

class CatalogRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var jsonDataSource: JsonDataSource
    private lateinit var favoritesDao: FavoritesDao

    @Before
    fun setup() {
        jsonDataSource = mockk(relaxed = true)
        favoritesDao = mockk(relaxed = true)
    }

    @Test
    fun `GIVEN datasource has 2 books  WHEN getBooks filters items case insensitively THEN return filtered list`() =
        runTest(testDispatcher) {
            // 1. Arrange
            val catalog = CatalogResponseDto(
                updatedAt = Instant.parse("2025-01-17T10:15:30Z"),
                items = listOf(
                    BookDto(
                        id = "1",
                        title = "Kotlin Programming",
                        category = "TECH",
                        price = 28.0,
                        rating = 4.8,
                    ),
                    BookDto(
                        id = "2",
                        title = "Android Development",
                        category = "TECH",
                        price = 39.99,
                        rating = 4.5,
                    )
                )
            )

            // Mock sources for the repository
            coEvery { jsonDataSource.getCatalog() } returns catalog
            every { favoritesDao.observeFavorites() } returns flowOf(emptyList())

            val repository = CatalogRepositoryImpl(jsonDataSource, favoritesDao, testDispatcher)

            // 2. Act: Query with uppercase "KOTLIN"
            val result = repository.getBooks(BookQuery("KOTLIN"))

            // 3. Assert
            assertTrue(result is DataResult.Success)
            val flow = (result as DataResult.Success).data

            flow.test {
                val emittedList = awaitItem()

                // Verify only 1 item is returned
                assertEquals(1, emittedList.size)
                assertEquals(catalog.items[0].id, emittedList[0].id)
                assertEquals(catalog.items[0].title, emittedList[0].title)

                awaitComplete()
            }
        }

    @Test
    fun `GIVEN non-existing id WHEN getBookById THEN return Success with null`() = runTest(testDispatcher) {
        // 1. Arrange
        val nonExistingId = "999"
        val catalog = CatalogResponseDto(
            updatedAt = Instant.parse("2025-01-17T10:15:30Z"),
            items = listOf(
                BookDto(
                    id = "1",
                    title = "Kotlin Programming",
                    category = "TECH",
                    price = 28.0,
                    rating = 4.8,
                ),
            )
        )

        // Mock sources for the repository
        coEvery { jsonDataSource.getCatalog() } returns catalog

        val repository = CatalogRepositoryImpl(jsonDataSource, favoritesDao, testDispatcher)

        // 2. Act
        val result = repository.getBookById(nonExistingId)

        // 3. Assert
        assertTrue(result is DataResult.Success)

        val successResult = result as DataResult.Success
        assertNull("Expected null book for non-existing ID", successResult.data)
    }

    @Test
    fun `GIVEN datasource has 2 books WHEN favorites updates THEN getBooks reflects favorite status reactively`() =
        runTest(testDispatcher) {
            // 1. Arrange
            val catalog = CatalogResponseDto(
                updatedAt = Instant.parse("2025-01-17T10:15:30Z"),
                items = listOf(
                    BookDto(
                        id = "1",
                        title = "Kotlin Programming",
                        category = "TECH",
                        price = 28.0,
                        rating = 4.8,
                    ),
                    BookDto(
                        id = "2",
                        title = "Android Development",
                        category = "TECH",
                        price = 39.99,
                        rating = 4.5,
                    )
                )
            )

            // Create a MutableStateFlow to simulate a live database stream
            val favoritesFlow = MutableStateFlow(listOf<FavoritesEntity>())

            // Mock sources for the repository
            coEvery { jsonDataSource.getCatalog() } returns catalog
            every { favoritesDao.observeFavorites() } returns favoritesFlow

            val repository = CatalogRepositoryImpl(jsonDataSource, favoritesDao, testDispatcher)

            // 2. Act
            val result = repository.getBooks(BookQuery(""))

            // 3. Assert
            assertTrue(result is DataResult.Success)

            val flow = (result as DataResult.Success).data

            flow.test {
                // Initial state: No favorites
                val firstEmission = awaitItem()
                assertEquals(false, firstEmission.find { it.id == "1" }?.isFavorite)
                assertEquals(false, firstEmission.find { it.id == "2" }?.isFavorite)

                // Simulate database update: Add book "1" to favorites
                favoritesFlow.value = listOf(FavoritesEntity("1"))

                // Verify the flow re-emits with the updated favorite status
                val secondEmission = awaitItem()
                assertEquals(true, secondEmission.find { it.id == "1" }?.isFavorite)
                assertEquals(false, secondEmission.find { it.id == "2" }?.isFavorite)

                // Simulate database update: Add book "2" as well
                favoritesFlow.value = listOf(FavoritesEntity("1"), FavoritesEntity("2"))

                val thirdEmission = awaitItem()
                assertEquals(true, thirdEmission.find { it.id == "1" }?.isFavorite)
                assertEquals(true, thirdEmission.find { it.id == "2" }?.isFavorite)

                cancelAndIgnoreRemainingEvents()
            }
        }


    @Test
    fun `GIVEN data source failure WHEN getBooks THEN return DataResult Error`() = runTest(testDispatcher) {
        // 1. Arrange
        val exception = IOException("")

        // Mock the data source to throw an exception
        coEvery { jsonDataSource.getCatalog() } throws exception

        val repository = CatalogRepositoryImpl(jsonDataSource, favoritesDao, testDispatcher)

        // 2. Act
        val result = repository.getBooks(BookQuery(""))

        // 3. Assert
        assertTrue("Expected DataResult.Error but was $result", result is DataResult.Error)

        val errorResult = result as DataResult.Error
        // Depending on your error mapping logic, verify the message or exception type
        assertEquals(CommonError.FileNotFoundError, errorResult.error)
    }


    @Test
    fun `GIVEN data source failure WHEN getBookById THEN return DataResult Error`() = runTest(testDispatcher) {
        // 1. Arrange
        val bookId = "1"
        val exception = RuntimeException("Failed to load catalog")

        // Mock the data source to throw an exception
        coEvery { jsonDataSource.getCatalog() } throws exception

        val repository = CatalogRepositoryImpl(jsonDataSource, favoritesDao, testDispatcher)

        // 2. Act
        val result = repository.getBookById(bookId)

        // 3. Assert
        assertTrue("Expected DataResult.Error but was $result", result is DataResult.Error)

        val errorResult = result as DataResult.Error
        // Verify that the error contains the expected exception
        assertEquals(CommonError.UnknownError(exception), errorResult.error)
    }


    @Test
    fun `GIVEN dao failure WHEN toggleFavoriteBook THEN return DataResult Error`() = runTest(testDispatcher) {
        // 1. Arrange
        val bookId = "1"
        val exception = RuntimeException("Database error")

        // Mock the DAO to throw an exception when called
        coEvery { favoritesDao.toggleFavorite(bookId) } throws exception

        val repository = CatalogRepositoryImpl(jsonDataSource, favoritesDao, testDispatcher)

        // 2. Act
        val result = repository.toggleFavoriteBook(bookId)

        // 3. Assert
        assertTrue("Expected DataResult.Error but was $result", result is DataResult.Error)

        val errorResult = result as DataResult.Error
        assertEquals(CommonError.UnknownError(exception), errorResult.error)
    }
}