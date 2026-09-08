package com.hsact.sunplanner.domain.repository

import com.hsact.sunplanner.data.responses.Location
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing location search history.
 */
interface HistoryRepository {
    /**
     * Flow emitting the current search history.
     */
    val history: Flow<List<Location>>

    /**
     * Adds a location to the search history.
     * Keeps only unique locations and limits the total count.
     */
    suspend fun addToHistory(location: Location)

    /**
     * Removes a specific location from the search history.
     */
    suspend fun removeFromHistory(location: Location)

    /**
     * Clears the entire search history.
     */
    suspend fun clearHistory()
}
