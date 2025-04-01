import com.hsact.sunplanner.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoService {
    @GET("v1/forecast")
    suspend fun getHistoricalWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("hourly") hourly: String
    ): WeatherResponse
}
