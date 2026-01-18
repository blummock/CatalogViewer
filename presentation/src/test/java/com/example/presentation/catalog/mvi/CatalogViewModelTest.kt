package com.example.presentation.catalog.mvi

import app.cash.turbine.test
import com.example.domain.entity.Book
import com.example.domain.entity.BookQuery
import com.example.domain.entity.Category
import com.example.domain.entity.DataResult
import com.example.domain.error.CommonError
import com.example.domain.repository.CatalogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CatalogViewModelTest {

    private lateinit var repository: CatalogRepository
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN viewModel with initial uiState WHEN data is loaded THEN the uiState is updated`() = runTest {
        // 1. Arrange
        val book = Book(
            id = "1",
            title = "The Blue Fox",
            category = Category.FICTION,
            price = 28.0.toBigDecimal(),
            rating = 4.8.toFloat(),
            isFavorite = false,
        )

        // Mock repository to return a book
        coEvery { repository.getBooks(any()) } returns DataResult.Success(flowOf(listOf(book)))

        // 2. Act
        val viewModel = CatalogViewModel(repository)

        // 3. Assert Book State
        viewModel.uiState.test {
            // Initial state is Loading
            assertEquals(BooksState.Loading, awaitItem().booksState)

            // Second state should Ready with book
            val readyState = awaitItem()
            assertTrue(readyState.booksState is BooksState.Ready)

            val bookState = readyState.booksState as BooksState.Ready
            assertEquals(book.id, bookState.books[0].id)
            assertEquals(book.title, bookState.books[0].title)
            assertEquals(book.isFavorite, bookState.books[0].isFavorite)
            assertEquals(book.category.name, bookState.books[0].category)
            assertEquals("28.00", bookState.books[0].price)
            assertEquals(book.rating.toString(), bookState.books[0].rating)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN viewModel with initial uiState WHEN getBooks fails THEN the booksState is Empty`() = runTest {
        // 1. Arrange
        val error = CommonError.FileNotFoundError

        // Mock repository to return an Error result
        coEvery { repository.getBooks(any()) } returns DataResult.Error(error)

        // 2. Act
        val viewModel = CatalogViewModel(repository)

        // 3. Assert UI State
        viewModel.uiState.test {
            // Initial state is Loading
            assertEquals(BooksState.Loading, awaitItem().booksState)

            // Second state should be Error
            val errorState = awaitItem().booksState
            assertTrue("Expected BooksState.Error but was $errorState", errorState is BooksState.Empty)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN viewModel WHEN query is changed THEN repository getBooks is called with correct query`() = runTest {
        // 1. Arrange
        val query = "Kotlin"

        val viewModel = CatalogViewModel(repository)

        // 2. Act
        viewModel.onAction(CatalogAction.EditQuery(query))
        testScheduler.advanceUntilIdle()

        // 3. Assert
        // Verify that the repository was called with the specific query string
        coVerify { repository.getBooks(BookQuery(query)) }

        // Also verify the UI state reflects the new query string
        viewModel.uiState.test {
            assertEquals(query, awaitItem().query)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN viewModel WHEN OnFavorite fails THEN effect is OnShowError`() = runTest {
        // 1. Arrange
        val bookId = "book_1"
        val error = CommonError.UnknownError(RuntimeException(""))

        // Mock toggleFavoriteBook to return our specific error
        coEvery { repository.toggleFavoriteBook(bookId) } returns DataResult.Error(error)

        val viewModel = CatalogViewModel(repository)

        // 2. Act
        viewModel.onAction(CatalogAction.OnFavorite(bookId))

        // 3. Assert
        viewModel.effect.test {
            val effect = awaitItem()

            // Verify the effect is OnShowError
            assertTrue("Expected CatalogEffect.OnShowError but was $effect", effect is CatalogEffect.OnShowError)

            // Verify the error message matches the error from repository
            val errorEffect = effect as CatalogEffect.OnShowError
            assertEquals(error.message, errorEffect.msg)

            cancelAndIgnoreRemainingEvents()
        }

        // Verify the repository method was actually called
        coVerify { repository.toggleFavoriteBook(bookId) }
    }
}