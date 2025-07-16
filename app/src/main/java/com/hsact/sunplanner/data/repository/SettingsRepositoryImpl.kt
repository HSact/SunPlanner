package com.hsact.sunplanner.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.domain.repository.SettingsRepository
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.WindSpeedUnitMode
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

/**
 * Implementation of [SettingsRepository] that manages user preferences using
 * Jetpack DataStore.
 *
 * This class provides reactive [Flow] properties to observe changes in settings
 * and suspend functions to update preferences.
 *
 * @property context The application context injected by Hilt.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SettingsRepository {

    companion object {
        private val THEME_KEY = intPreferencesKey("theme")
        private val LANGUAGE_KEY = intPreferencesKey("language")
        private val TEMPERATURE_UNIT_KEY = intPreferencesKey("temperature_unit")
        private val WIND_SPEED_UNIT_KEY = intPreferencesKey("wind_unit")
        private val PRECIPITATION_UNIT_KEY = intPreferencesKey("precipitation_unit")
        private val IS_DOTS_VISIBLE_KEY = booleanPreferencesKey("is_dots_visible")
        private val IS_GRAPH_CURVED_KEY = booleanPreferencesKey("is_graph_curved")
        private val LOCATION_KEY = stringPreferencesKey("location")
    }

    /**
     * Flow of the current [ThemeMode] preference.
     * Emits updates when the theme setting changes.
     */
    override val theme: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY]?.let { ThemeMode.entries[it] } ?: ThemeMode.SYSTEM
    }

    /**
     * Flow of the current [LanguageMode] preference.
     * Emits updates when the language setting changes.
     */
    override val language: Flow<LanguageMode?> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY]?.let { LanguageMode.entries[it] }
    }

    /**
     * Flow of the current [TemperatureUnitMode] preference.
     * Emits updates when the temperature unit changes.
     */
    override val temperatureUnit: Flow<TemperatureUnitMode> =
        context.dataStore.data.map { preferences ->
            preferences[TEMPERATURE_UNIT_KEY]?.let { TemperatureUnitMode.entries[it] }
                ?: TemperatureUnitMode.CELSIUS
        }

    /**
     * Flow of the current [WindSpeedUnitMode] preference.
     * Emits updates when the wind speed unit changes.
     */
    override val windSpeedUnit: Flow<WindSpeedUnitMode> =
        context.dataStore.data.map { preferences ->
            preferences[WIND_SPEED_UNIT_KEY]?.let { WindSpeedUnitMode.entries[it] }
                ?: WindSpeedUnitMode.MS
        }

    /**
     * Flow of the current [PrecipitationUnitMode] preference.
     * Emits updates when the precipitation unit changes.
     */
    override val precipitationUnit: Flow<PrecipitationUnitMode> =
        context.dataStore.data.map { preferences ->
            preferences[PRECIPITATION_UNIT_KEY]?.let { PrecipitationUnitMode.entries[it] }
                ?: PrecipitationUnitMode.MM
        }

    /**
     * Flow indicating whether dots are visible on graphs.
     * Defaults to `true` if no preference is saved.
     */
    @Suppress("NullableBooleanElvis")
    override val isDotsVisible: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[IS_DOTS_VISIBLE_KEY] ?: true
        }

    /**
     * Flow indicating whether graphs are drawn with curved lines.
     * Defaults to `true` if no preference is saved.
     */
    @Suppress("NullableBooleanElvis")
    override val isGraphCurved: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[IS_GRAPH_CURVED_KEY] ?: true
        }

    /**
     * Flow of the saved [Location], or `null` if none is saved.
     * The location is stored as a JSON string and parsed using Moshi.
     */
    override val location: Flow<Location?> =
        context.dataStore.data.map { prefs ->
            prefs[LOCATION_KEY]?.let { json ->
                runCatching {
                    Moshi.Builder().build().adapter(Location::class.java).fromJson(json)
                }.getOrNull()
            }
        }

    /**
     * Saves the [ThemeMode] preference.
     *
     * @param themeMode The theme mode to save.
     */
    override suspend fun setTheme(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeMode.ordinal
        }
    }

    /**
     * Saves the [LanguageMode] preference.
     *
     * @param languageMode The language mode to save.
     */
    override suspend fun setLanguage(languageMode: LanguageMode) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageMode.ordinal
        }
    }

    /**
     * Saves the [TemperatureUnitMode] preference.
     *
     * @param temperatureMode The temperature unit to save.
     */
    override suspend fun setTemperatureUnit(temperatureMode: TemperatureUnitMode) {
        context.dataStore.edit { preferences ->
            preferences[TEMPERATURE_UNIT_KEY] = temperatureMode.ordinal
        }
    }

    /**
     * Saves the [WindSpeedUnitMode] preference.
     *
     * @param windMode The wind speed unit to save.
     */
    override suspend fun setWindSpeedUnit(windMode: WindSpeedUnitMode) {
        context.dataStore.edit { preferences ->
            preferences[WIND_SPEED_UNIT_KEY] = windMode.ordinal
        }
    }

    /**
     * Saves the [PrecipitationUnitMode] preference.
     *
     * @param precipitationMode The precipitation unit to save.
     */
    override suspend fun setPrecipitationUnit(precipitationMode: PrecipitationUnitMode) {
        context.dataStore.edit { preferences ->
            preferences[PRECIPITATION_UNIT_KEY] = precipitationMode.ordinal
        }
    }

    /**
     * Saves the preference for dots visibility on graphs.
     *
     * @param isVisible `true` to show dots, `false` to hide.
     */
    override suspend fun setDotsVisibility(isVisible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DOTS_VISIBLE_KEY] = isVisible
        }
    }

    /**
     * Saves the preference for graph curvature.
     *
     * @param isCurved `true` to use curved graphs, `false` otherwise.
     */
    override suspend fun setGraphCurved(isCurved: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_GRAPH_CURVED_KEY] = isCurved
        }
    }

    /**
     * Saves the [Location] as a JSON string.
     *
     * @param location The location object to save.
     */
    override suspend fun setLocation(location: Location) {
        val adapter = Moshi.Builder().build().adapter(Location::class.java)
        val json = adapter.toJson(location)
        context.dataStore.edit { preferences ->
            preferences[LOCATION_KEY] = json
        }
    }
}