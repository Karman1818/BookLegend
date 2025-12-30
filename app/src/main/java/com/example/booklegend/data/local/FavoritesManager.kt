package com.example.booklegend.data.local

import android.content.Context
import android.content.SharedPreferences

class FavoritesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("book_prefs", Context.MODE_PRIVATE)
    private val key = "favorite_books_ids"

    fun addFavorite(id: String) {
        val currentList = getFavorites().toMutableSet()
        currentList.add(id)
        saveList(currentList)
    }

    fun removeFavorite(id: String) {
        val currentList = getFavorites().toMutableSet()
        currentList.remove(id)
        saveList(currentList)
    }

    fun isFavorite(id: String): Boolean {
        return getFavorites().contains(id)
    }

    fun getFavorites(): Set<String> {
        return prefs.getStringSet(key, emptySet()) ?: emptySet()
    }

    private fun saveList(list: Set<String>) {
        prefs.edit().putStringSet(key, list).apply()
    }
}