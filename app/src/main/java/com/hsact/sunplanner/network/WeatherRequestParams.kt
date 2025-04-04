package com.hsact.sunplanner.network

data class WeatherRequestParams(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var startDate: String = "",
    var endDate: String = ""
)