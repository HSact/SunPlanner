package com.hsact.sunplanner.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hsact.sunplanner.domain.model.Bookmark
import com.hsact.sunplanner.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : BookmarkRepository {
    private val BOOKMARKS_KEY = stringPreferencesKey("bookmarks_list")

    override val bookmarks: Flow<List<Bookmark>> = dataStore.data.map { preferences ->
        val json = preferences[BOOKMARKS_KEY] ?: "[]"
        try {
            Json.decodeFromString<List<Bookmark>>(json)
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error decoding bookmarks", e)
            emptyList()
        }
    }

    override suspend fun addBookmark(bookmark: Bookmark) {
        dataStore.edit { preferences ->
            val currentJson = preferences[BOOKMARKS_KEY] ?: "[]"
            val currentList = try {
                Json.decodeFromString<List<Bookmark>>(currentJson).toMutableList()
            } catch (e: Exception) {
                Log.e("BookmarkRepo", "Error decoding bookmarks during add", e)
                mutableListOf()
            }

            // Avoid duplicates
            if (currentList.none { it.id == bookmark.id }) {
                currentList.add(bookmark)
                preferences[BOOKMARKS_KEY] = Json.encodeToString(currentList)
            }
        }
    }

    override suspend fun removeBookmark(id: String) {
        dataStore.edit { preferences ->
            val currentJson = preferences[BOOKMARKS_KEY] ?: "[]"
            val currentList = try {
                Json.decodeFromString<List<Bookmark>>(currentJson).toMutableList()
            } catch (e: Exception) {
                Log.e("BookmarkRepo", "Error decoding bookmarks during remove", e)
                mutableListOf()
            }

            if (currentList.removeIf { it.id == id }) {
                preferences[BOOKMARKS_KEY] = Json.encodeToString(currentList)
            }
        }
    }

    override suspend fun isBookmarked(locationName: String, dates: String): Boolean {
        // Implementation for quick check if needed
        return false
    }

    override suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.remove(BOOKMARKS_KEY)
        }
    }
}
