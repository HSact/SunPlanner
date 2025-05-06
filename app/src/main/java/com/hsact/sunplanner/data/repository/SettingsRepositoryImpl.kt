package com.hsact.sunplanner.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hsact.sunplanner.data.responses.Location
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

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    companion object {
        private val THEME_KEY = intPreferencesKey("theme")
        private val LANGUAGE_KEY = intPreferencesKey("language")
        private val TEMPERATURE_UNIT_KEY = intPreferencesKey("temperature_unit")
        private val WIND_SPEED_UNIT_KEY = intPreferencesKey("wind_unit")
        private val PRECIPITATION_UNIT_KEY = intPreferencesKey("precipitation_unit")
        private val LOCATION_KEY = stringPreferencesKey("location")
    }

    override val theme: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY]?.let { ThemeMode.entries[it] } ?: ThemeMode.SYSTEM
    }

    override val language: Flow<LanguageMode> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY]?.let { LanguageMode.entries[it] } ?: LanguageMode.ENGLISH
    }
    override val temperatureUnit: Flow<TemperatureUnitMode> =
        context.dataStore.data.map { preferences ->
            preferences[TEMPERATURE_UNIT_KEY]?.let { TemperatureUnitMode.entries[it] }
                ?: TemperatureUnitMode.CELSIUS
        }
    override val windSpeedUnit: Flow<WindSpeedUnitMode> =
        context.dataStore.data.map { preferences ->
            preferences[WIND_SPEED_UNIT_KEY]?.let { WindSpeedUnitMode.entries[it] }
                ?: WindSpeedUnitMode.MS
        }
    override val precipitationUnit: Flow<PrecipitationUnitMode> =
        context.dataStore.data.map { preferences ->
            preferences[PRECIPITATION_UNIT_KEY]?.let { PrecipitationUnitMode.entries[it] }
                ?: PrecipitationUnitMode.MM
        }
    override val location: Flow<Location?> =
        context.dataStore.data.map { prefs ->
        prefs[LOCATION_KEY]?.let { json ->
            runCatching { Moshi.Builder().build().adapter(Location::class.java).fromJson(json) }.getOrNull()
        }
    }

    override suspend fun setTheme(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeMode.ordinal
        }
    }

    override suspend fun setLanguage(languageMode: LanguageMode) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageMode.ordinal
        }
    }

    override suspend fun setTemperatureUnit(temperatureMode: TemperatureUnitMode) {
        context.dataStore.edit { preferences ->
            preferences[TEMPERATURE_UNIT_KEY] = temperatureMode.ordinal
        }
    }

    override suspend fun setWindSpeedUnit(windSpeedMode: WindSpeedUnitMode) {
        context.dataStore.edit { preferences ->
            preferences[WIND_SPEED_UNIT_KEY] = windSpeedMode.ordinal
        }
    }

    override suspend fun setPrecipitationUnit(precipitationMode: PrecipitationUnitMode) {
        context.dataStore.edit { preferences ->
            preferences[PRECIPITATION_UNIT_KEY] = precipitationMode.ordinal
        }
    }

    override suspend fun setLocation (location: Location) {
        val adapter = Moshi.Builder().build().adapter(Location::class.java)
        val json = adapter.toJson(location)
        context.dataStore.edit { preferences ->
            preferences[LOCATION_KEY] = json
        }
    }
}