package com.example.booklegend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.booklegend.R
import com.example.booklegend.data.model.BookDetails
import com.example.booklegend.ui.viewmodel.BookViewModel
import com.example.booklegend.ui.viewmodel.DetailUiState
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String,
    viewModel: BookViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    LaunchedEffect(bookId) {
        viewModel.getBookDetails(bookId)
    }

    val state by viewModel.detailUiState.collectAsState()
    // obserwujemy stan ulubionych z ViewModelu
    val isFavorite by viewModel.isBookFavorite.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły książki") },
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
                is DetailUiState.Loading -> CircularProgressIndicator()
                is DetailUiState.Error -> Text(text = s.message, color = MaterialTheme.colorScheme.error)
                is DetailUiState.Success -> BookDetailContent(
                    details = s.details,
                    isFavorite = isFavorite,
                    onToggleFavorite = { viewModel.toggleFavorite(bookId) }
                )
            }
        }
    }
}

@Composable
fun BookDetailContent(
    details: BookDetails,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // okladka
        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.height(300.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(details.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // tytul
        Text(
            text = details.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AssistChip(onClick = {}, label = { Text("Stron: ${details.pages}") })
            Spacer(modifier = Modifier.width(16.dp))
            AssistChip(onClick = {}, label = { Text("Rok: ${details.year}") })
        }

        Spacer(modifier = Modifier.height(24.dp))

        // przycisk ulubionych ktory juz w pelni dziala
        Button(
            onClick = onToggleFavorite,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isFavorite) "Usuń z ulubionych" else "Dodaj do ulubionych")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // opis
        Text(
            text = "Opis",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = details.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
    }
}