package com.example.weatherapp.Repository

import android.util.Log
import com.example.weatherapp.Exception.WeatherDataFetchException
import com.example.weatherapp.data.WeatherData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

class WeatherRepository {
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(Interceptor { chain ->
            val request = chain.request()
            Log.d("MyApp", "Отправляется запрос: ${request.url}") // Логируем URL запроса

            val response = chain.proceed(request)
            response
        })
        .build()


    suspend fun fetchWeatherData(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): WeatherData {
        try {
            val baseUrl = "https://api.openweathermap.org/data/2.5/"
            val endpoint = "weather"
            val url = "$baseUrl$endpoint?lat=$latitude&lon=$longitude&appid=$apiKey"

            val request = Request.Builder()
                .url(url)
                .build()

            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            val responseString = response.body?.string()

            Log.d("MyApp", "URL: $url")
            Log.d("MyApp", "Response: $responseString")

            if (response.isSuccessful) {
                val gson = Gson()
                val weatherData = gson.fromJson(responseString, WeatherData::class.java)
                Log.d("MyApp", "WeatherData: $weatherData")
                return weatherData
            } else {
                Log.w("MyApp", "Unsuccessful response: ${response.code}")
                throw WeatherDataFetchException()
            }
        } catch (e: Exception) {
            Log.e("MyApp", "Error fetching weather data: ${e.message}")
            throw WeatherDataFetchException()
        }
    }


}
