package com.example.booklegend.data.network

import com.example.booklegend.data.model.BookDetailsDto
import com.example.booklegend.data.model.SearchResponse // <--- Dodaj import
import com.example.booklegend.data.model.SubjectResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApi {

    // lista domyslna offset
    @GET("subjects/fiction.json")
    suspend fun getFictionBooks(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): SubjectResponse

    // wyszukiwanie page
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): SearchResponse

    // szczegoly
    @GET("works/{workId}.json")
    suspend fun getBookDetails(
        @Path("workId") workId: String
    ): BookDetailsDto
}