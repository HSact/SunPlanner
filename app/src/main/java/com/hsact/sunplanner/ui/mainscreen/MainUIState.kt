package com.hsact.sunplanner.ui.mainscreen

import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.domain.error.ApiError
import com.hsact.sunplanner.domain.model.DatesBundle
import com.hsact.sunplanner.domain.model.SettingsBundle
import com.hsact.sunplanner.domain.model.WeatherMetrics
import java.time.LocalDate

/**
 * Represents the UI state for the main screen.
 *
 * Encapsulates all the necessary data related to location, date range, weather data,
 * errors, and user settings used for historical weather analysis.
 *
 * @property settingsBundle Currently selected user settings (language, units, theme, etc.).
 * @property maxYearRange Maximum allowed year range for statistics.
 * @property validationError Optional validation message for incorrect user input.
 * @property networkError Optional error returned during data fetching.
 * @property networkErrorId Optional string resource ID for error message (for localization support).
 * @property isLoading Indicates whether data is currently being fetched.
 * @property isOneDay True if the user has selected a single day, false if a range.
 * @property isOneYear True if the user is requesting stats for one year.
 * @property cityName Name of the city entered by the user.
 * @property cities List of possible city matches for the entered city name.
 * @property tempDates Temporary date selection (used before confirmation).
 * @property confirmedDates Confirmed date selection (used for querying data).
 * @property weatherData Raw weather response received from the API.
 * @property weatherMetrics Preprocessed weather metrics derived from raw weather data.
 */
data class MainUIState(
    val settingsBundle: SettingsBundle = SettingsBundle(),
    val maxYearRange: Int = 30,
    val validationError: String? = null,
    val networkError: ApiError? = null,
    val networkErrorId: String? = null,
    val isLoading: Boolean = false,
    val cityName: String = "",
    val cities: List<Location> = emptyList(),
    val tempDates: DatesBundle = DatesBundle(
        LocalDate.now().minusYears(10),
        LocalDate.now().plusDays(13).minusYears(1)
    ),
    val confirmedDates: DatesBundle = tempDates,
    val weatherData: WeatherResponse? = null,
    val weatherMetrics: WeatherMetrics = WeatherMetrics(),
) {

    /**
     * Checks whether the user's location is defined in the settings.
     */
    val isLocationNotNull: Boolean
        get() = settingsBundle.location != null

    /**
     * True if the selected start and end dates have the same month and day.
     * Year is ignored.
     */
    val isOneDay: Boolean
        get() = confirmedDates.start.month == confirmedDates.end.month &&
                confirmedDates.start.dayOfMonth == confirmedDates.end.dayOfMonth

    /**
     * True if the selected start and end dates are in the same year.
     */
    val isOneYear: Boolean
        get() = confirmedDates.start.year == confirmedDates.end.year
}