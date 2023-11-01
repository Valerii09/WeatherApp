package com.example.weatherapp.data

import com.example.weatherapp.Service.Api.GeocodingService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GeocodingRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.opencagedata.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val geocodingService = retrofit.create(GeocodingService::class.java)

    suspend fun getCityByCoordinates(latitude: Double, longitude: Double, apiKey: String): String {
        val coordinates = "$latitude,$longitude"
        val response = geocodingService.getCityByCoordinates(coordinates, apiKey)

        if (response.isSuccessful) {
            val geocodingData = response.body()
            val city = geocodingData?.results?.firstOrNull()?.components?.city
            return city ?: "City not found"
        } else {
            // Обработка ошибки запроса к API
            return "City not found"
        }
    }
}
