package com.example.booklegend

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.booklegend.ui.screens.BookDetailScreen
import com.example.booklegend.ui.screens.FavoritesScreen
import com.example.booklegend.ui.screens.HomeScreen
import com.example.booklegend.ui.theme.BookLegendTheme
import com.example.booklegend.ui.viewmodel.BookViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: BookViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentLanguage by viewModel.currentLanguage.collectAsState()

            LocalizedApp(language = currentLanguage) {
                BookLegendTheme(darkTheme = isDarkMode) {
                    BookAppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun LocalizedApp(language: String, content: @Composable () -> Unit) {
    val locale = remember(language) { Locale(language) }
    val currentConfig = LocalConfiguration.current
    val configuration = remember(language, currentConfig) {
        Configuration(currentConfig).apply {
            setLocale(locale)
        }
    }
    val context = LocalContext.current
    val localizedContext = remember(language, context) {
        context.createConfigurationContext(configuration)
    }

    LaunchedEffect(language) {
        Locale.setDefault(locale)
    }

    CompositionLocalProvider(
        LocalConfiguration provides configuration,
        LocalContext provides localizedContext
    ) {
        content()
    }
}

@Composable
fun BookAppNavigation(viewModel: BookViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // home screen
        composable(
            route = "home",
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(500))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500))
            }
        ) {
            HomeScreen(
                viewModel = viewModel,
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
            ),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(500))
            }
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

        // Favorites screen
        composable(
            route = "favorites",
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(500))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(500))
            }
        ) {
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
