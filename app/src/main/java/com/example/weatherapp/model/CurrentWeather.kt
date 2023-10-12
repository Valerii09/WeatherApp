package com.example.weatherapp.model

data class CurrentWeather(
    val time: Long,
    val interval: Int,
    val temperature: Double,
    val windspeed: Double,
    val winddirection: Int,
    val sunrise: Long,
    val sunset: Long,
    val is_day: Int,
    val relativehumidity_2m: Int,
    val pressure_msl: Double,
    val weathercode: Int

)