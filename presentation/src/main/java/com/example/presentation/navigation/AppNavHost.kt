package com.example.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.presentation.catalog.mvi.CatalogViewModel
import com.example.presentation.catalog.ui.CatalogScreen
import com.example.presentation.details.mvi.DetailsViewModel
import com.example.presentation.details.ui.DetailsScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Catalog.route,
    ) {
        composable(AppRoute.Catalog.route) {
            val viewModel: CatalogViewModel = hiltViewModel()
            CatalogScreen(viewModel, goToDetails = { bookId ->
                navController.navigate(AppRoute.Details.create(bookId))
            })
        }

        composable(
            route = AppRoute.Details.route,
            arguments = listOf(
                navArgument(AppRoute.Details.BOOK_ID) {
                    type = NavType.StringType
                }
            )
        ) {
            val viewModel: DetailsViewModel = hiltViewModel()
            DetailsScreen(viewModel)
        }
    }
}