package com.example.booklegend.data.repository

import com.example.booklegend.data.model.Book
import com.example.booklegend.data.model.BookDetails
import com.example.booklegend.data.network.BookApi
import com.example.booklegend.data.network.RetrofitClient

class BookRepository(
    private val api: BookApi = RetrofitClient.api
) {

    // funkcja obslugująca lite domyslna i wyszukiwarke
    suspend fun getBooks(offset: Int = 0, query: String = ""): List<Book> {
        return if (query.isEmpty()) {
            // brak wpisanego tekstu to Pobierz kategorię fiction
            val response = api.getFictionBooks(limit = 20, offset = offset)

            response.works.map { workDto ->
                Book(
                    id = workDto.key.replace("/works/", ""),
                    title = workDto.title,
                    authorName = workDto.authors?.firstOrNull()?.name ?: "Nieznany autor",
                    coverUrl = workDto.coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" },
                    year = workDto.firstPublishYear?.toString() ?: "Brak daty"
                )
            }
        } else {
            // uzytkownik wpisal tekst -> uzyj API wyszukiwania
            val page = (offset / 20) + 1

            val response = api.searchBooks(query = query, page = page, limit = 20)

            response.docs.map { doc ->
                Book(
                    id = doc.key.replace("/works/", ""),
                    title = doc.title,
                    authorName = doc.authorNames?.firstOrNull() ?: "Nieznany autor",
                    coverUrl = doc.coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" },
                    year = doc.firstPublishYear?.toString() ?: "Brak daty"
                )
            }
        }
    }

    // szczegoly
    suspend fun getBookDetails(bookId: String): BookDetails {
        val dto = api.getBookDetails(bookId)

        val descriptionText = when (val desc = dto.description) {
            is String -> desc
            is Map<*, *> -> desc["value"] as? String ?: "Brak opisu"
            else -> "Brak opisu"
        }

        val coverUrl = dto.covers?.firstOrNull()?.let { id ->
            "https://covers.openlibrary.org/b/id/$id-L.jpg"
        }

        return BookDetails(
            title = dto.title,
            authorName = null,
            description = descriptionText,
            coverUrl = coverUrl,
            pages = dto.numberOfPages?.toString() ?: "Brak danych",
            year = dto.firstPublishDate ?: "Brak daty"
        )
    }
}