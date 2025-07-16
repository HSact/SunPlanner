package com.hsact.sunplanner.data.network

import com.hsact.sunplanner.data.responses.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for accessing historical weather data
 * from the Open-Meteo API.
 */
interface OpenMeteoService {

    /**
     * Fetches historical daily weather data for a given location and date range.
     *
     * @param latitude Latitude of the location.
     * @param longitude Longitude of the location.
     * @param startDate Start date in the format "yyyy-MM-dd".
     * @param endDate End date in the format "yyyy-MM-dd".
     * @param daily Comma-separated list of daily weather variables to retrieve.
     *              Default includes weather code, temperature, wind, etc.
     * @param temperatureUnit Unit for temperature values (e.g., "celsius" or "fahrenheit").
     * @param windSpeedUnit Unit for wind speed (e.g., "kmh", "ms", "mph", "kn").
     * @param precipitationUnit Unit for precipitation (e.g., "mm" or "inch").
     * @param timezone Timezone to use for date alignment (e.g., "auto").
     *
     * @return A [WeatherResponse] object containing the weather data.
     */
    @GET("v1/archive")
    suspend fun getHistoricalWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,precipitation_sum,precipitation_hours,sunshine_duration,daylight_duration,wind_speed_10m_max,wind_gusts_10m_max",
        @Query("temperature_unit") temperatureUnit: String = "celsius",
        @Query("wind_speed_unit") windSpeedUnit: String = "ms",
        @Query("precipitation_unit") precipitationUnit: String = "mm",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}