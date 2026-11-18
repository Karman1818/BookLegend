package com.example.booklegend.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.booklegend.data.model.Book
import com.example.booklegend.ui.screens.BookItem
import com.example.booklegend.ui.screens.EmptyStateView
import com.example.booklegend.ui.screens.ErrorView
import com.example.booklegend.ui.theme.BookLegendTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookComponentsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bookItem_displaysTitleAuthorAndYear() {
        val testBook = Book(
            id = "OL123W",
            title = "The Fellowship of the Ring",
            authorName = "J.R.R. Tolkien",
            coverUrl = null,
            year = "1954"
        )

        composeTestRule.setContent {
            BookLegendTheme {
                BookItem(book = testBook, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("The Fellowship of the Ring").assertIsDisplayed()
        composeTestRule.onNodeWithText("J.R.R. Tolkien").assertIsDisplayed()
        composeTestRule.onNodeWithText("1954").assertIsDisplayed()
    }

    @Test
    fun bookItem_click_invokesCallback() {
        var clickedId = ""
        val testBook = Book(
            id = "OL456W",
            title = "Brave New World",
            authorName = "Aldous Huxley",
            coverUrl = null,
            year = "1932"
        )

        composeTestRule.setContent {
            BookLegendTheme {
                BookItem(book = testBook, onClick = { clickedId = testBook.id })
            }
        }

        composeTestRule.onNodeWithText("Brave New World").performClick()
        assertEquals("OL456W", clickedId)
    }

    @Test
    fun emptyStateView_displaysSearchQueryText() {
        composeTestRule.setContent {
            BookLegendTheme {
                EmptyStateView(query = "NonExistentBookXYZ")
            }
        }

        // Checks that query text is visible in empty state
        composeTestRule.onNodeWithText("NonExistentBookXYZ", substring = true).assertIsDisplayed()
    }

    @Test
    fun errorView_displaysErrorMessageAndHandlesRetry() {
        var retried = false

        composeTestRule.setContent {
            BookLegendTheme {
                ErrorView(
                    message = "Connection timed out",
                    onRetry = { retried = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Connection timed out").assertIsDisplayed()
        // Retry button click
        composeTestRule.onNode(hasClickAction()).performClick()
        assertEquals(true, retried)
    }
}
