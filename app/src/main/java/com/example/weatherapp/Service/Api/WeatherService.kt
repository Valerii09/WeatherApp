package com.example.weatherapp.Service.Api


import com.example.weatherapp.model.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface WeatherService {
    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String,
        @Query("current_weather") currentWeather: Boolean,
        @Query("timeformat") timeformat: String,
        @Query("timezone") timezone: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<WeatherData>
}
