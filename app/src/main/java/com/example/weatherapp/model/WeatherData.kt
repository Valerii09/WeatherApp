package com.example.weatherapp.model

data class WeatherData(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val current_weather_units: WeatherUnits,
    val current_weather: CurrentWeather,
    val daily_units: WeatherUnits,
    val daily: DailyWeather
)