package com.example.weatherapp.Repository

import android.util.Log
import com.example.weatherapp.Exception.WeatherDataFetchException
import com.example.weatherapp.Service.Api.ApiConstants
import com.example.weatherapp.Service.Api.WeatherApi
import com.example.weatherapp.model.WeatherData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class WeatherRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(WeatherApi::class.java)

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
        try {
            Log.d("WeatherRepository", "Sending request for latitude: $latitude, longitude: $longitude")

            val response = api.getWeather(
                latitude,
                longitude,
                daily,
                currentWeather,
                timeformat,
                timezone,
                startDate,
                endDate
            )

            if (response != null) {
                Log.d("WeatherRepository", "Response data: $response")
                Log.d("WeatherRepository", "Received successful response")
                return response
            } else {
                Log.e("WeatherRepository", "Received error response")
                throw WeatherDataFetchException()
            }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error while fetching weather data: $e")
            throw WeatherDataFetchException()
        }
    }
}

