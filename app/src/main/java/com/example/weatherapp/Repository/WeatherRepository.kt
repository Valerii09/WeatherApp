package com.example.weatherapp.Repository

import com.example.weatherapp.Service.WeatherService
import com.example.weatherapp.WeatherDataFetchException
import com.example.weatherapp.WeatherDataNotFoundException
import com.example.weatherapp.model.WeatherData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/") // Замените на ваш API URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(WeatherService::class.java)

    suspend fun fetchWeatherData(
        latitude: Double,
        longitude: Double,
        daily: String,
        currentWeather: Boolean,
        timeformat: String,
        timezone: String,
        startDate: String,
        endDate: String
    ): WeatherData {
        val response = service.getWeather(latitude, longitude, daily, currentWeather, timeformat, timezone, startDate, endDate)

        if (response.isSuccessful) {
            return response.body() ?: throw WeatherDataNotFoundException()
        } else {
            throw WeatherDataFetchException()
        }
    }
}

