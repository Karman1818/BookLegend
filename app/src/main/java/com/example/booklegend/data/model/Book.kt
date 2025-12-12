package com.example.booklegend.data.model

data class Book(
    val id: String,          // czyste ID"
    val title: String,
    val authorName: String,  // laczymy autorow w jeden ciag
    val coverUrl: String?,   // URL do obrazka
    val year: String
)

data class BookDetails(
    val title: String,
    val authorName: String?, // przekazujemy z listy bo api czasem nie zwraca autorow
    val description: String,
    val coverUrl: String?,
    val pages: String,
    val year: String
)