package com.hsact.sunplanner.domain.repository

import com.hsact.sunplanner.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing saved weather search bookmarks.
 */
interface BookmarkRepository {
    /**
     * Flow emitting the current list of bookmarks.
     */
    val bookmarks: Flow<List<Bookmark>>

    /**
     * Saves a new bookmark to the list.
     */
    suspend fun addBookmark(bookmark: Bookmark)

    /**
     * Removes a bookmark by its unique ID.
     */
    suspend fun removeBookmark(id: String)

    /**
     * Checks if a bookmark already exists for the given location and dates.
     */
    suspend fun isBookmarked(locationName: String, dates: String): Boolean

    /**
     * Removes all saved bookmarks.
     */
    suspend fun clearAll()
}
