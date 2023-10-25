package com.example.weatherapp.Service.Api


import com.example.weatherapp.data.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApi {
    @GET("https://api.openweathermap.org/data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): WeatherData
}
