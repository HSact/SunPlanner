package com.hsact.sunplanner

import OpenMeteoService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hsact.sunplanner.ui.theme.SunPlannerTheme
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SunPlannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        val moshi = Moshi.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        val service: OpenMeteoService = retrofit.create(OpenMeteoService::class.java)
        fetchWeather(service)
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SunPlannerTheme {
        Greeting("Android")
    }
}

fun fetchWeather(service: OpenMeteoService) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = service.getHistoricalWeather(
                latitude = 55.7522,
                longitude = 37.6156,
                startDate = "2010-01-01",
                endDate = "2019-12-31",
                hourly = "temperature_2m"
            )
            println(response)
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }
}