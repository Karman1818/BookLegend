package com.example.booklegend.data.network

import com.example.booklegend.data.model.BookDetailsDto
import com.example.booklegend.data.model.SubjectResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApi {

    // Pobieramy liste ksiązek
    // Endpoint: subjects/fiction.json?limit=20
    @GET("subjects/fiction.json")
    suspend fun getFictionBooks(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): SubjectResponse

    // Pobieramy szczegoly konkretnej ksiazki
    // Endpoint: works/{work_id}.json
    @GET("works/{workId}.json")
    suspend fun getBookDetails(
        @Path("workId") workId: String
    ): BookDetailsDto
}