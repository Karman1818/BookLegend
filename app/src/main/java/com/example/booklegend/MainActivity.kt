package com.example.booklegend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.booklegend.ui.screens.BookDetailScreen
import com.example.booklegend.ui.screens.FavoritesScreen
import com.example.booklegend.ui.screens.HomeScreen
import com.example.booklegend.ui.theme.BookLegendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookLegendTheme {
                BookAppNavigation()
            }
        }
    }
}

@Composable
fun BookAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // home screen
        composable("home") {
            HomeScreen(
                onBookClick = { bookId ->
                    navController.navigate("detail/$bookId")
                },
                onFavoritesClick = {
                    navController.navigate("favorites")
                }
            )
        }

        // detail screen
        composable(
            route = "detail/{bookId}",
            arguments = listOf(
                navArgument("bookId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")

            if (bookId != null) {
                BookDetailScreen(
                    bookId = bookId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // favorites screen
        composable("favorites") {
            FavoritesScreen(
                onBookClick = { bookId ->
                    navController.navigate("detail/$bookId")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}