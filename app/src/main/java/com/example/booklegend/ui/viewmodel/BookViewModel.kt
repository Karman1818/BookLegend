package com.example.booklegend.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.booklegend.data.local.FavoritesManager
import com.example.booklegend.data.model.Book
import com.example.booklegend.data.model.BookDetails
import com.example.booklegend.data.repository.BookRepository
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

// dostajemy application w konstruktorze
class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BookRepository()

    // inicjalizujemy nasz manager
    private val favoritesManager = FavoritesManager(application.applicationContext)

    // stan home
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // stan szczegolow
    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailUiState: StateFlow<DetailUiState> = _detailUiState.asStateFlow()

    // stan ulubionych
    private val _isBookFavorite = MutableStateFlow(false)
    val isBookFavorite: StateFlow<Boolean> = _isBookFavorite.asStateFlow()

    init {
        getBooks()
    }

    fun getBooks() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val books = repository.getBooks()
                if (books.isEmpty()) {
                    _uiState.value = HomeUiState.Error("Lista książek jest pusta.")
                } else {
                    _uiState.value = HomeUiState.Success(books)
                }
            } catch (e: IOException) {
                _uiState.value = HomeUiState.Error("Brak połączenia z internetem.")
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Wystąpił błąd: ${e.localizedMessage}")
            }
        }
    }

    fun getBookDetails(bookId: String) {
        // sprawdzamy czy jest ulubiona
        checkIfFavorite(bookId)

        // pobieramy dane z sieci
        viewModelScope.launch {
            _detailUiState.value = DetailUiState.Loading
            try {
                val details = repository.getBookDetails(bookId)
                _detailUiState.value = DetailUiState.Success(details)
            } catch (e: IOException) {
                _detailUiState.value = DetailUiState.Error("Brak połączenia z internetem.")
            } catch (e: Exception) {
                _detailUiState.value = DetailUiState.Error("Nie udało się pobrać szczegółów.")
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