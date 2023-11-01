package com.example.weatherapp.model.openWeather

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)