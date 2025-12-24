package com.example.booklegend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booklegend.data.model.Book
import com.example.booklegend.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// definiujemy stan
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val books: List<Book>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class BookViewModel(
    // wrzucamy repo
    private val repository: BookRepository = BookRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // pobieramy ksiazki od razu przy starcie ViewModelu
        getBooks()
    }

    fun getBooks() {
        viewModelScope.launch {
            // ustawiamy stan na loading
            _uiState.value = HomeUiState.Loading

            try {
                // wywolujemy funkcje repo
                val books = repository.getBooks()

                if (books.isEmpty()) {
                    _uiState.value = HomeUiState.Error("Lista książek jest pusta.")
                } else {
                    _uiState.value = HomeUiState.Success(books)
                }
            } catch (e: IOException) {
                _uiState.value = HomeUiState.Error("Brak połączenia z internetem.")
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Wystąpił nieoczekiwany błąd: ${e.localizedMessage}")
            }
        }
    }
}