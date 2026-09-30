package com.hsact.sunplanner.data.network

/**
 * Represents the request parameters used to fetch historical weather data.
 */
data class WeatherRequestParams(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var startDate: String = "",
    var endDate: String = "",
    var temperatureUnit: String = "celsius",
    var windSpeedUnit: String = "ms",
    var precipitationUnit: String = "mm",
    var daily: String = DEFAULT_DAILY_VARIABLES,
    var timezone: String = "auto"
) {
    companion object {
        const val DEFAULT_DAILY_VARIABLES =
            "weather_code,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,precipitation_sum,precipitation_hours,sunshine_duration,daylight_duration,wind_speed_10m_max,wind_gusts_10m_max"
    }
}
