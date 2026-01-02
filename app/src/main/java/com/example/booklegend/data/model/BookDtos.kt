package com.example.booklegend.data.model

import com.google.gson.annotations.SerializedName


data class SubjectResponse(
    val name: String,
    val works: List<BookWorkDto>
)

data class BookWorkDto(
    val title: String,
    val key: String,
    @SerializedName("cover_id") val coverId: Long?,
    @SerializedName("first_publish_year") val firstPublishYear: Int?,
    val authors: List<AuthorDto>?
)

data class AuthorDto(val name: String)

data class BookDetailsDto(
    val title: String,
    val description: Any?,
    val covers: List<Long>?,
    @SerializedName("number_of_pages") val numberOfPages: Int?,
    @SerializedName("first_publish_date") val firstPublishDate: String?,
)

// klasy wyszukiwarki

data class SearchResponse(
    val docs: List<SearchDocDto>
)

data class SearchDocDto(
    val key: String,
    val title: String,
    // W API search autorzy są lista stringow
    @SerializedName("author_name") val authorNames: List<String>?,
    @SerializedName("cover_i") val coverId: Long?,
    @SerializedName("first_publish_year") val firstPublishYear: Int?
)