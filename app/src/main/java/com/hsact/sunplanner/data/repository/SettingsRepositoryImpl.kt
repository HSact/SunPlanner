package com.hsact.sunplanner.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.domain.model.LanguageMode
import com.hsact.sunplanner.domain.model.PrecipitationUnitMode
import com.hsact.sunplanner.domain.model.TemperatureUnitMode
import com.hsact.sunplanner.domain.model.ThemeMode
import com.hsact.sunplanner.domain.model.WindSpeedUnitMode
import com.hsact.sunplanner.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [SettingsRepository] that manages user preferences using
 * Jetpack DataStore.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
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

    override val theme: Flow<ThemeMode> = dataStore.data.map { preferences ->
        preferences[THEME_KEY]?.let { ThemeMode.fromIndex(it) } ?: ThemeMode.SYSTEM
    }

    override val language: Flow<LanguageMode?> = dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY]?.let { LanguageMode.fromIndex(it) }
    }

    override val temperatureUnit: Flow<TemperatureUnitMode> =
        dataStore.data.map { preferences ->
            preferences[TEMPERATURE_UNIT_KEY]?.let { TemperatureUnitMode.fromIndex(it) }
                ?: TemperatureUnitMode.CELSIUS
        }

    override val windSpeedUnit: Flow<WindSpeedUnitMode> =
        dataStore.data.map { preferences ->
            preferences[WIND_SPEED_UNIT_KEY]?.let { WindSpeedUnitMode.fromIndex(it) }
                ?: WindSpeedUnitMode.MS
        }

    override val precipitationUnit: Flow<PrecipitationUnitMode> =
        dataStore.data.map { preferences ->
            preferences[PRECIPITATION_UNIT_KEY]?.let { PrecipitationUnitMode.fromIndex(it) }
                ?: PrecipitationUnitMode.MM
        }

    @Suppress("NullableBooleanElvis")
    override val isDotsVisible: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[IS_DOTS_VISIBLE_KEY] ?: true
        }

    @Suppress("NullableBooleanElvis")
    override val isGraphCurved: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[IS_GRAPH_CURVED_KEY] ?: true
        }

    override val location: Flow<Location?> =
        dataStore.data.map { prefs ->
            prefs[LOCATION_KEY]?.let { json ->
                runCatching {
                    Json.decodeFromString<Location>(json)
                }.getOrNull()
            }
        }

    override suspend fun setTheme(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeMode.toIndex()
        }
    }

    override suspend fun setLanguage(languageMode: LanguageMode) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageMode.toIndex()
        }
    }

    override suspend fun setTemperatureUnit(temperatureMode: TemperatureUnitMode) {
        dataStore.edit { preferences ->
            preferences[TEMPERATURE_UNIT_KEY] = temperatureMode.toIndex()
        }
    }

    override suspend fun setWindSpeedUnit(windMode: WindSpeedUnitMode) {
        dataStore.edit { preferences ->
            preferences[WIND_SPEED_UNIT_KEY] = windMode.toIndex()
        }
    }

    override suspend fun setPrecipitationUnit(precipitationMode: PrecipitationUnitMode) {
        dataStore.edit { preferences ->
            preferences[PRECIPITATION_UNIT_KEY] = precipitationMode.toIndex()
        }
    }

    override suspend fun setDotsVisibility(isVisible: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DOTS_VISIBLE_KEY] = isVisible
        }
    }

    override suspend fun setGraphCurved(isCurved: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_GRAPH_CURVED_KEY] = isCurved
        }
    }

    override suspend fun setLocation(location: Location) {
        val json = Json.encodeToString(location)
        dataStore.edit { preferences ->
            preferences[LOCATION_KEY] = json
        }
    }
}
