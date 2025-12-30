package com.example.booklegend.data.model

import com.google.gson.annotations.SerializedName

// Odpowiedź z listy książek
// URL: https://openlibrary.org/subjects/fiction.json?limit=20
data class SubjectResponse(
    val name: String,
    val works: List<BookWorkDto>
)

data class BookWorkDto(
    val title: String,
    val key: String, // nasze ID

    @SerializedName("cover_id")
    val coverId: Long?,

    @SerializedName("first_publish_year")
    val firstPublishYear: Int?,

    val authors: List<AuthorDto>?
)

data class AuthorDto(
    val name: String
)

// Odpowiedź ze szczegółów książki
// URL: https://openlibrary.org/works/{work_id}.json
data class BookDetailsDto(
    val title: String,

    // Api zwraca string lub obiekt.
    val description: Any?,

    val covers: List<Long>?,

    @SerializedName("number_of_pages")
    val numberOfPages: Int?,

    @SerializedName("first_publish_date")
    val firstPublishDate: String?,
)