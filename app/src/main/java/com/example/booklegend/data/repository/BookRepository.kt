package com.example.booklegend.data.repository

import com.example.booklegend.data.model.Book
import com.example.booklegend.data.model.BookDetails
import com.example.booklegend.data.network.BookApi
import com.example.booklegend.data.network.RetrofitClient

class BookRepository(
    // wrzucamy api
    private val api: BookApi = RetrofitClient.api
) {

    // funkcja pobierajaca i mapujaca liste ksiazek
    suspend fun getBooks(): List<Book> {
        val response = api.getFictionBooks()

        // Mapowanie: DTO do Book
        return response.works.map { workDto ->
            // czyszczenie klucza api
            val cleanId = workDto.key.replace("/works/", "")

            // buidowanie url okladki
            val coverUrl = workDto.coverId?.let { id ->
                "https://covers.openlibrary.org/b/id/$id-M.jpg"
            }

            Book(
                id = cleanId,
                title = workDto.title,
                authorName = workDto.authors?.firstOrNull()?.name ?: "Nieznany autor",
                coverUrl = coverUrl,
                year = workDto.firstPublishYear?.toString() ?: "Brak daty"
            )
        }
    }

    // funkcja pobierająca szczegoly
    suspend fun getBookDetails(bookId: String): BookDetails {
        val dto = api.getBookDetails(bookId)

        // obsluga opisu
        val descriptionText = when (val desc = dto.description) {
            is String -> desc
            is Map<*, *> -> desc["value"] as? String ?: "Brak opisu" // Jeśli obiekt JSON
            else -> "Brak opisu"
        }

        val coverUrl = dto.covers?.firstOrNull()?.let { id ->
            "https://covers.openlibrary.org/b/id/$id-L.jpg" // L.jpg to large okładka
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