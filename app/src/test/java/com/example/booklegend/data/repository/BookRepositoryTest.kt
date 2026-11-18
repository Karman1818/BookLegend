package com.example.booklegend.data.repository

import com.example.booklegend.data.model.AuthorDto
import com.example.booklegend.data.model.BookDetailsDto
import com.example.booklegend.data.model.BookWorkDto
import com.example.booklegend.data.model.SearchDocDto
import com.example.booklegend.data.model.SearchResponse
import com.example.booklegend.data.model.SubjectResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class BookRepositoryTest {

    private lateinit var fakeApi: FakeBookApi
    private lateinit var repository: BookRepository

    @Before
    fun setUp() {
        fakeApi = FakeBookApi()
        repository = BookRepository(api = fakeApi)
    }

    @Test
    fun `getBooks with empty query returns mapped fiction books`() = runBlocking {
        fakeApi.subjectResponseToReturn = SubjectResponse(
            name = "fiction",
            works = listOf(
                BookWorkDto(
                    title = "The Hobbit",
                    key = "/works/OL262758W",
                    coverId = 12345L,
                    firstPublishYear = 1937,
                    authors = listOf(AuthorDto(name = "J.R.R. Tolkien"))
                )
            )
        )

        val result = repository.getBooks(offset = 0, query = "")

        assertEquals(1, result.size)
        val book = result.first()
        assertEquals("OL262758W", book.id)
        assertEquals("The Hobbit", book.title)
        assertEquals("J.R.R. Tolkien", book.authorName)
        assertEquals("https://covers.openlibrary.org/b/id/12345-M.jpg", book.coverUrl)
        assertEquals("1937", book.year)
    }

    @Test
    fun `getBooks with negative cover ID returns null coverUrl`() = runBlocking {
        fakeApi.subjectResponseToReturn = SubjectResponse(
            name = "fiction",
            works = listOf(
                BookWorkDto(
                    title = "No Cover Book",
                    key = "/works/OL999W",
                    coverId = -1L,
                    firstPublishYear = 2020,
                    authors = null
                )
            )
        )

        val result = repository.getBooks(offset = 0, query = "")

        assertEquals(1, result.size)
        assertNull(result.first().coverUrl)
        assertNull(result.first().authorName)
    }

    @Test
    fun `getBooks with query calls searchBooks and maps search results`() = runBlocking {
        fakeApi.searchResponseToReturn = SearchResponse(
            docs = listOf(
                SearchDocDto(
                    key = "works/OL888W",
                    title = "Dune",
                    authorNames = listOf("Frank Herbert"),
                    coverId = 54321L,
                    firstPublishYear = 1965
                )
            )
        )

        val result = repository.getBooks(offset = 0, query = "Dune")

        assertEquals(1, result.size)
        val book = result.first()
        assertEquals("OL888W", book.id)
        assertEquals("Dune", book.title)
        assertEquals("Frank Herbert", book.authorName)
        assertEquals("https://covers.openlibrary.org/b/id/54321-M.jpg", book.coverUrl)
        assertEquals("1965", book.year)
    }

    @Test
    fun `getBookDetails parses string description properly`() = runBlocking {
        fakeApi.bookDetailsToReturn = BookDetailsDto(
            title = "1984",
            description = "A dystopian novel",
            covers = listOf(9999L),
            numberOfPages = 328,
            firstPublishDate = "1949"
        )

        val details = repository.getBookDetails("OL1984W")

        assertEquals("1984", details.title)
        assertEquals("A dystopian novel", details.description)
        assertEquals("https://covers.openlibrary.org/b/id/9999-L.jpg", details.coverUrl)
        assertEquals("328", details.pages)
        assertEquals("1949", details.year)
    }

    @Test
    fun `getBookDetails parses map description properly`() = runBlocking {
        fakeApi.bookDetailsToReturn = BookDetailsDto(
            title = "Fahrenheit 451",
            description = mapOf("type" to "/type/text", "value" to "Guy Montag is a fireman"),
            covers = listOf(-1L, 7777L),
            numberOfPages = 158,
            firstPublishDate = "1953"
        )

        val details = repository.getBookDetails("OL451W")

        assertEquals("Fahrenheit 451", details.title)
        assertEquals("Guy Montag is a fireman", details.description)
        // Should ignore -1 and pick 7777
        assertEquals("https://covers.openlibrary.org/b/id/7777-L.jpg", details.coverUrl)
    }

    @Test
    fun `getBookDetails handles null fields gracefully`() = runBlocking {
        fakeApi.bookDetailsToReturn = BookDetailsDto(
            title = "Minimal Book",
            description = null,
            covers = null,
            numberOfPages = null,
            firstPublishDate = null
        )

        val details = repository.getBookDetails("OL000W")

        assertEquals("Minimal Book", details.title)
        assertNull(details.description)
        assertNull(details.coverUrl)
        assertNull(details.pages)
        assertNull(details.year)
    }
}
