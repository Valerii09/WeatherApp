package com.example.weatherapp.Service.Api

import com.example.weatherapp.view.geocoding.GeocodingData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("geocode/v1/json")
    suspend fun getCityByCoordinates(
        @Query("q") coordinates: String,  // Пример: "55.0415,82.934"
        @Query("key") apiKey: String
    ): Response<GeocodingData>
}