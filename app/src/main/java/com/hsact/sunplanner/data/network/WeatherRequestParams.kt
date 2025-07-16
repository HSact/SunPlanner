package com.hsact.sunplanner.data.network

/**
 * Represents the request parameters used to fetch historical weather data.
 *
 * @property latitude Geographic latitude of the location.
 * @property longitude Geographic longitude of the location.
 * @property startDate Start date of the data request in format "yyyy-MM-dd".
 * @property endDate End date of the data request in format "yyyy-MM-dd".
 * @property temperatureUnit Unit for temperature values (e.g., "celsius", "fahrenheit").
 * @property windSpeedUnit Unit for wind speed values (e.g., "kmh", "ms", "mph", "kn").
 * @property precipitationUnit Unit for precipitation values (e.g., "mm", "inch").
 */
data class WeatherRequestParams(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var startDate: String = "",
    var endDate: String = "",
    var temperatureUnit: String = "",
    var windSpeedUnit: String = "",
    var precipitationUnit: String = ""
)