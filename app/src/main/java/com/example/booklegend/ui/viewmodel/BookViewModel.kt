package com.example.booklegend.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.booklegend.data.local.FavoritesDataStore
import com.example.booklegend.data.model.Book
import com.example.booklegend.data.model.BookDetails
import com.example.booklegend.data.repository.BookRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// stany

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val books: List<Book>,
        val isRefreshing: Boolean = false,
        val isLoadingMore: Boolean = false,
        val isLastPage: Boolean = false
    ) : HomeUiState
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

@OptIn(ExperimentalCoroutinesApi::class)
class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BookRepository()
    private val favoritesDataStore = FavoritesDataStore(application.applicationContext)

    // paginacja
    private var currentOffset = 0
    private var currentBooks = mutableListOf<Book>()
    private var isLastPage = false
    private var isLoadingMore = false

    // wyszukiwanie i ladowanie
    private var currentQuery = ""
    private var searchJob: Job? = null
    private var loadJob: Job? = null

    // stany ui

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailUiState: StateFlow<DetailUiState> = _detailUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // strumien ID ulubionych
    val favoriteIds: StateFlow<Set<String>> = favoritesDataStore.favoritesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // strumien trybu ciemnego
    val isDarkMode: StateFlow<Boolean> = favoritesDataStore.darkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // strumien jezyka aplikacji
    val currentLanguage: StateFlow<String> = favoritesDataStore.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "pl")

    // strumien UI ulubionych
    val favoritesUiState: StateFlow<FavoritesUiState> = favoritesDataStore.favoritesFlow
        .transformLatest { ids ->
            emit(FavoritesUiState.Loading)
            if (ids.isEmpty()) {
                emit(FavoritesUiState.Empty)
            } else {
                try {
                    val books = coroutineScope {
                        val deferredBooks = ids.map { id ->
                            async {
                                try {
                                    val details = repository.getBookDetails(id)
                                    Book(
                                        id = id,
                                        title = details.title,
                                        authorName = details.authorName,
                                        coverUrl = details.coverUrl,
                                        year = details.year
                                    )
                                } catch (e: Exception) {
                                    if (e is CancellationException) throw e
                                    null
                                }
                            }
                        }
                        deferredBooks.awaitAll().filterNotNull()
                    }
                    if (books.isEmpty()) emit(FavoritesUiState.Empty)
                    else emit(FavoritesUiState.Success(books))
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    emit(FavoritesUiState.Error(e.localizedMessage ?: "Error"))
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FavoritesUiState.Loading)

    init {
        loadFirstPage()
    }

    // funkcje

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            currentQuery = newQuery
            loadFirstPage()
        }
    }

    fun loadFirstPage() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            currentOffset = 0
            isLastPage = false

            try {
                val books = repository.getBooks(offset = 0, query = currentQuery)
                currentBooks.clear()
                currentBooks.addAll(books)
                _uiState.value = HomeUiState.Success(currentBooks.toList())
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.value = HomeUiState.Error(e.localizedMessage ?: "Error")
            }
        }
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val oldList = (uiState.value as? HomeUiState.Success)?.books ?: emptyList()
            _uiState.value = HomeUiState.Success(oldList, isRefreshing = true)
            currentOffset = 0
            isLastPage = false

            try {
                val books = repository.getBooks(offset = 0, query = currentQuery)
                currentBooks.clear()
                currentBooks.addAll(books)
                _uiState.value = HomeUiState.Success(currentBooks.toList(), isRefreshing = false)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.value = HomeUiState.Success(currentBooks.toList(), isRefreshing = false)
            }
        }
    }

    fun loadNextPage() {
        if (isLoadingMore || isLastPage) return
        val currentSuccess = _uiState.value as? HomeUiState.Success ?: return

        viewModelScope.launch {
            isLoadingMore = true
            _uiState.value = currentSuccess.copy(isLoadingMore = true)
            currentOffset += 20

            try {
                val newBooks = repository.getBooks(offset = currentOffset, query = currentQuery)
                if (newBooks.isEmpty()) {
                    isLastPage = true
                    _uiState.value = currentSuccess.copy(isLoadingMore = false, isLastPage = true)
                } else {
                    currentBooks.addAll(newBooks)
                    _uiState.value = HomeUiState.Success(
                        books = currentBooks.toList(),
                        isLoadingMore = false,
                        isLastPage = false
                    )
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                currentOffset -= 20
                _uiState.value = currentSuccess.copy(isLoadingMore = false)
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun getBookDetails(bookId: String) {
        viewModelScope.launch {
            _detailUiState.value = DetailUiState.Loading
            try {
                val details = repository.getBookDetails(bookId)
                _detailUiState.value = DetailUiState.Success(details)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _detailUiState.value = DetailUiState.Error(e.localizedMessage ?: "Error")
            }
        }
    }

    fun toggleFavorite(bookId: String) {
        viewModelScope.launch {
            favoritesDataStore.toggleFavorite(bookId)
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val current = isDarkMode.value
            favoritesDataStore.setDarkMode(!current)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            favoritesDataStore.setLanguage(language)
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val next = if (currentLanguage.value == "pl") "en" else "pl"
            favoritesDataStore.setLanguage(next)
        }
    }
}
