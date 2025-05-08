package com.hsact.sunplanner.ui.mainscreen

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.domain.model.WeatherGraphData
import com.hsact.sunplanner.ui.settings.modes.LanguageMode
import com.hsact.sunplanner.ui.settings.modes.ThemeMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.PrecipitationUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.TemperatureUnitMode
import com.hsact.sunplanner.ui.settings.modes.unitModes.WindSpeedUnitMode
import java.time.LocalDate

data class MainUIState (
    val languageMode: LanguageMode = LanguageMode.ENGLISH,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val temperatureUnitMode: TemperatureUnitMode = TemperatureUnitMode.CELSIUS,
    val windUnitMode: WindSpeedUnitMode = WindSpeedUnitMode.MS,
    val precipitationUnitMode: PrecipitationUnitMode = PrecipitationUnitMode.MM,
    val error : String = "",
    val isLoading: Boolean = false,
    val isOneDay: Boolean = true,
    val isOneYear: Boolean = false,
    val cityName: String = "",
    val cities: List<Location> = emptyList(),
    val location: Location? = null,
    val startLD: LocalDate = LocalDate.now().minusYears(10),
    val endLD: LocalDate = LocalDate.now().minusYears(1),
    val confirmedStartLD: LocalDate = startLD,
    val confirmedEndLD: LocalDate = endLD,
    val weatherData: WeatherResponse? = null,
    val weatherGraphData: WeatherGraphData = WeatherGraphData()
)