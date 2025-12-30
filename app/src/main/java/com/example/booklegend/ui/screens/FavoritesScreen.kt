package com.example.booklegend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booklegend.ui.viewmodel.BookViewModel
import com.example.booklegend.ui.viewmodel.FavoritesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: BookViewModel = viewModel(),
    onBookClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    // odswiez liste przy kazdym wejsciu na ten ekran
    LaunchedEffect(Unit) {
        viewModel.getFavoriteBooks()
    }

    val state by viewModel.favoritesUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ulubione książki") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val s = state) {
                is FavoritesUiState.Loading -> CircularProgressIndicator()
                is FavoritesUiState.Empty -> {
                    Text(
                        text = "Brak ulubionych książek.\nDodaj coś z listy głównej!",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is FavoritesUiState.Error -> {
                    Text(text = s.message, color = MaterialTheme.colorScheme.error)
                }
                is FavoritesUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(s.favoriteBooks) { book ->
                            // reuzywamy BookItem z HomeScreen
                            BookItem(book = book, onClick = { onBookClick(book.id) })
                        }
                    }
                }
            }
        }
    }
}