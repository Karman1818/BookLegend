package com.example.booklegend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booklegend.data.model.Book
import com.example.booklegend.data.model.BookDetails
import com.example.booklegend.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// stan dla listy (HomeScreen)
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val books: List<Book>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

// stan dla szczegolow (BookDetailScreen)
sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val details: BookDetails) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class BookViewModel(
    private val repository: BookRepository = BookRepository()
) : ViewModel() {

    // logika home
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // logika szczegolow
    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailUiState: StateFlow<DetailUiState> = _detailUiState.asStateFlow()

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

    // pobieranie szczegolow ksiazek
    fun getBookDetails(bookId: String) {
        viewModelScope.launch {
            _detailUiState.value = DetailUiState.Loading
            try {
                // wywolanie funkcji z repozytorium
                val details = repository.getBookDetails(bookId)
                _detailUiState.value = DetailUiState.Success(details)
            } catch (e: IOException) {
                _detailUiState.value = DetailUiState.Error("Brak połączenia z internetem.")
            } catch (e: Exception) {
                _detailUiState.value = DetailUiState.Error("Nie udało się pobrać szczegółów.")
            }
        }
    }
}