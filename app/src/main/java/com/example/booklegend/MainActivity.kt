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
    //tworzymy kontroler nawigacji
    val navController = rememberNavController()

    //definiujemy kontener na ekrany
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        //lista
        composable("home") {
            HomeScreen(
                onBookClick = { bookId ->
                    //nawigacja do szczegolow z przekazaniem parametru
                    navController.navigate("detail/$bookId")
                }
            )
        }

        //szczegoly

        //definiujemy parametr bookId w sciezce
        composable(
            route = "detail/{bookId}",
            arguments = listOf(
                navArgument("bookId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            //wyciagamy bookId z argumentow
            val bookId = backStackEntry.arguments?.getString("bookId")

            if (bookId != null) {
                BookDetailScreen(
                    bookId = bookId,
                    onBackClick = {
                        //powrot do poprzedniego ekranu
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}