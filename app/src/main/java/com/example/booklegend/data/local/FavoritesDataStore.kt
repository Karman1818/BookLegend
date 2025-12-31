package com.example.booklegend.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class FavoritesDataStore(private val context: Context) {

    private val FAVORITE_KEYS = stringSetPreferencesKey("favorite_books_ids")

    val favoritesFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITE_KEYS] ?: emptySet()
        }

    suspend fun toggleFavorite(id: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITE_KEYS] ?: emptySet()
            if (current.contains(id)) {
                preferences[FAVORITE_KEYS] = current - id
            } else {
                preferences[FAVORITE_KEYS] = current + id
            }
        }
    }
}