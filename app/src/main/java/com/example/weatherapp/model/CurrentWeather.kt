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
    val relativehumidity_2m: Int, // Добавлено для Relative Humidity (2 m)
    val pressure_msl: Double, // Добавлено для Sea Level Pressure
    val weathercode: Int

)