package com.example.weatherapp.model

data class CurrentWeather(
    val time: Long,
    val interval: Int,
    val temperature: Double,
    val windspeed: Double,
    val winddirection: Int,
    val is_day: Int,
    val weathercode: Int
)