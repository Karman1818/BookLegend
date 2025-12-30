package com.example.booklegend.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.booklegend.data.local.FavoritesManager
import com.example.booklegend.data.model.Book
import com.example.booklegend.data.model.BookDetails
import com.example.booklegend.data.repository.BookRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// stany

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val books: List<Book>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val details: BookDetails) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState
    data class Success(val favoriteBooks: List<Book>) : FavoritesUiState
    data object Empty : FavoritesUiState
    data class Error(val message: String) : FavoritesUiState
}

class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BookRepository()
    private val favoritesManager = FavoritesManager(application.applicationContext)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailUiState: StateFlow<DetailUiState> = _detailUiState.asStateFlow()

    private val _isBookFavorite = MutableStateFlow(false)
    val isBookFavorite: StateFlow<Boolean> = _isBookFavorite.asStateFlow()

    private val _favoritesUiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val favoritesUiState: StateFlow<FavoritesUiState> = _favoritesUiState.asStateFlow()

    init {
        getBooks()
    }

    //funkcje

    fun getBooks() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val books = repository.getBooks()
                if (books.isEmpty()) _uiState.value = HomeUiState.Error("Lista książek jest pusta.")
                else _uiState.value = HomeUiState.Success(books)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Błąd: ${e.localizedMessage}")
            }
        }
    }

    fun getBookDetails(bookId: String) {
        checkIfFavorite(bookId)
        viewModelScope.launch {
            _detailUiState.value = DetailUiState.Loading
            try {
                val details = repository.getBookDetails(bookId)
                _detailUiState.value = DetailUiState.Success(details)
            } catch (e: Exception) {
                _detailUiState.value = DetailUiState.Error("Nie udało się pobrać szczegółów.")
            }
        }
    }

    fun getFavoriteBooks() {
        viewModelScope.launch {
            _favoritesUiState.value = FavoritesUiState.Loading
            val favoriteIds = favoritesManager.getFavorites()

            if (favoriteIds.isEmpty()) {
                _favoritesUiState.value = FavoritesUiState.Empty
                return@launch
            }

            try {
                val deferredBooks = favoriteIds.map { id ->
                    async {
                        try {
                            val details = repository.getBookDetails(id)
                            Book(
                                id = id,
                                title = details.title,
                                authorName = details.authorName ?: "Autor nieznany",
                                coverUrl = details.coverUrl,
                                year = details.year
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                }

                val books = deferredBooks.awaitAll().filterNotNull()

                if (books.isEmpty()) {
                    _favoritesUiState.value = FavoritesUiState.Error("Nie udało się pobrać danych ulubionych.")
                } else {
                    _favoritesUiState.value = FavoritesUiState.Success(books)
                }
            } catch (e: Exception) {
                _favoritesUiState.value = FavoritesUiState.Error("Błąd sieci: ${e.localizedMessage}")
            }
        }
    }

    private fun checkIfFavorite(bookId: String) {
        _isBookFavorite.value = favoritesManager.isFavorite(bookId)
    }

    fun toggleFavorite(bookId: String) {
        if (favoritesManager.isFavorite(bookId)) {
            favoritesManager.removeFavorite(bookId)
            _isBookFavorite.value = false
        } else {
            favoritesManager.addFavorite(bookId)
            _isBookFavorite.value = true
        }
    }
}