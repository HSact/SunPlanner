package com.hsact.sunplanner.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [HistoryRepository] using DataStore Preferences for persistence.
 * Stores search history as a JSON-serialized list of [Location] objects.
 */
@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : HistoryRepository {
    companion object {
        private val HISTORY_KEY = stringPreferencesKey("search_history")
        private const val MAX_HISTORY_SIZE = 10
    }

    override val history: Flow<List<Location>> = dataStore.data.map { preferences ->
        val json = preferences[HISTORY_KEY] ?: "[]"
        try {
            Json.decodeFromString<List<Location>>(json)
        } catch (e: Exception) {
            Log.e("HistoryRepo", "Error decoding search history", e)
            emptyList()
        }
    }

    override suspend fun addToHistory(location: Location) {
        dataStore.edit { preferences ->
            val currentJson = preferences[HISTORY_KEY] ?: "[]"
            val currentList = try {
                Json.decodeFromString<List<Location>>(currentJson).toMutableList()
            } catch (e: Exception) {
                Log.e("HistoryRepo", "Error decoding history during add", e)
                mutableListOf()
            }

            // Remove if already exists to move it to the top
            currentList.removeIf { it.latitude == location.latitude && it.longitude == location.longitude }
            currentList.add(0, location)

            // Limit size
            val limitedList = if (currentList.size > MAX_HISTORY_SIZE) {
                currentList.take(MAX_HISTORY_SIZE)
            } else {
                currentList
            }

            preferences[HISTORY_KEY] = Json.encodeToString(limitedList)
        }
    }

    override suspend fun removeFromHistory(location: Location) {
        dataStore.edit { preferences ->
            val currentJson = preferences[HISTORY_KEY] ?: "[]"
            val currentList = try {
                Json.decodeFromString<List<Location>>(currentJson).toMutableList()
            } catch (e: Exception) {
                Log.e("HistoryRepo", "Error decoding history during remove", e)
                mutableListOf()
            }

            val removed =
                currentList.removeIf { it.latitude == location.latitude && it.longitude == location.longitude }
            if (removed) {
                preferences[HISTORY_KEY] = Json.encodeToString(currentList)
            }
        }
    }

    override suspend fun clearHistory() {
        dataStore.edit { preferences ->
            preferences.remove(HISTORY_KEY)
        }
    }
}
