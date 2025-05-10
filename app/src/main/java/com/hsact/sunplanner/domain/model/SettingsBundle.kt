package com.hsact.sunplanner.domain.model

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.WindSpeedUnitMode

data class SettingsBundle(
    val location: Location? = null,
    val isDotsVisible: Boolean = true,
    val isEdgesCurved: Boolean = true,
    val languageMode: LanguageMode = LanguageMode.ENGLISH,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val temperatureUnitMode: TemperatureUnitMode = TemperatureUnitMode.CELSIUS,
    val windUnitMode: WindSpeedUnitMode = WindSpeedUnitMode.MS,
    val precipitationUnitMode: PrecipitationUnitMode = PrecipitationUnitMode.MM
)