package com.example.weatherapp.Service.Api


import com.example.weatherapp.model.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApi {
    @GET(ApiConstants.END_POINT)
    suspend fun getWeather(
        @Query(ApiParameters.LATITUDE) latitude: Double,
        @Query(ApiParameters.LONGITUDE) longitude: Double,
        @Query(ApiParameters.DAILY) daily: String,
        @Query(ApiParameters.CURRENT_WEATHER) currentWeather: Boolean,
        @Query(ApiParameters.TIME_FORMAT) timeformat: String,
        @Query(ApiParameters.TIMEZONE) timezone: String,
        @Query(ApiParameters.START_DATE) startDate: String,
        @Query(ApiParameters.END_DATE) endDate: String
    ): WeatherData
}
