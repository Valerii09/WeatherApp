package com.example.weatherapp.Repository

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.*
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class OpenCageRepository {
    private val client = Builder()
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

    suspend fun geocodeCity(cityName: String): Pair<Double, Double>? {
        try {
            val baseUrl = "https://api.opencagedata.com/geocode/v1/json"
            val apiKey = "74723a40510345568e67145a7679f85c"
            val url = "$baseUrl?q=${Uri.encode(cityName)}&key=$apiKey"

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
                val jsonObject = JSONObject(responseString)
                val results = jsonObject.getJSONArray("results")
                if (results.length() > 0) {
                    val geometry = results.getJSONObject(0).getJSONObject("geometry")
                    val lat = geometry.getDouble("lat")
                    val lng = geometry.getDouble("lng")
                    return Pair(lat, lng)
                } else {
                    Log.w("MyApp", "No results found in the response.")
                    return null
                }
            } else {
                Log.w("MyApp", "Unsuccessful response: ${response.code}")
                return null
            }
        } catch (e: Exception) {
            Log.e("MyApp", "Error geocoding city: ${e.message}")
            return null
        }
    }
}
