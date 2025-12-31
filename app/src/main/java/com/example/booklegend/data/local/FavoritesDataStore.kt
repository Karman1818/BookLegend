package com.example.booklegend.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class FavoritesDataStore(private val context: Context) {

    private val FAVORITE_KEYS = stringSetPreferencesKey("favorite_books_ids")
    private val DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")

    val favoritesFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITE_KEYS] ?: emptySet()
        }

    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
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


    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDark
        }
    }
}