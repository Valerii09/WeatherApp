package com.example.weatherapp.model

data class DailyWeather(
    val time: List<Long>,
    val weathercode: List<Int>,
    val temperature_2m_max: List<Double>,
    val apparent_temperature_max: List<Double>,
    val windspeed_10m_max: List<Double>,
    val winddirection_10m_dominant: List<Int>,
    val sunrise: List<Long>, // Добавлено для Sunrise
    val sunset:List<Long> ,
    val uv_index_max: List<Double>,
    val rain_sum: List<Double>// Добавлено для Sunset

)