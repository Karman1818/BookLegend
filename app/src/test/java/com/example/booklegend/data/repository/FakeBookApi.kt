package com.example.booklegend.data.repository

import com.example.booklegend.data.model.BookDetailsDto
import com.example.booklegend.data.model.SearchResponse
import com.example.booklegend.data.model.SubjectResponse
import com.example.booklegend.data.network.BookApi

class FakeBookApi : BookApi {
    var subjectResponseToReturn = SubjectResponse(name = "fiction", works = emptyList())
    var searchResponseToReturn = SearchResponse(docs = emptyList())
    var bookDetailsToReturn = BookDetailsDto(
        title = "Default Title",
        description = null,
        covers = null,
        numberOfPages = null,
        firstPublishDate = null
    )
    var shouldThrowError = false

    override suspend fun getFictionBooks(limit: Int, offset: Int): SubjectResponse {
        if (shouldThrowError) throw RuntimeException("Network error")
        return subjectResponseToReturn
    }

    override suspend fun searchBooks(query: String, page: Int, limit: Int): SearchResponse {
        if (shouldThrowError) throw RuntimeException("Network error")
        return searchResponseToReturn
    }

    override suspend fun getBookDetails(workId: String): BookDetailsDto {
        if (shouldThrowError) throw RuntimeException("Network error")
        return bookDetailsToReturn
    }
}
