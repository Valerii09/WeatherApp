package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.Service.GeocodingService
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.example.weatherapp.data.Geocoding.GeocodingRepository
import com.example.weatherapp.model.WeatherData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        // Наблюдайте за LiveData из ВьюМодели и обновляйте представление при изменении данных
        viewModel.getWeatherData().observe(this, Observer { weatherData ->
            updateUI(weatherData)
        })

        // Замените значения широты и долготы на нужные
        val latitude = 55.0
        val longitude = 82.875
        val geocodingRepository = GeocodingRepository()
        val apiKey = "74723a40510345568e67145a7679f85c" // Замените на свой ключ API
        val url = "https://api.opencagedata.com/geocode/v1/json?q=$latitude+$longitude&key=$apiKey"

        val geocodingService = Retrofit.Builder()
            .baseUrl("https://api.opencagedata.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = geocodingService.getCityByCoordinates("$latitude,$longitude", apiKey)
                if (response.isSuccessful) {
                    val geocodingData = response.body()
                    val city = geocodingData?.results?.firstOrNull()?.components?.city

                    // Обновляем UI с полученным городом
                    runOnUiThread {
                        if (!city.isNullOrBlank()) {
                            val cityTextView = findViewById<TextView>(R.id.cityTextView)
                            cityTextView.text = "City: $city"
                        } else {
                            // Если город не найден
                        }
                    }
                } else {
                    // Ошибка при запросе к API
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Обработка ошибок
            }
        }

        val daily = "weathercode,temperature_2m_max,apparent_temperature_max,windspeed_10m_max,winddirection_10m_dominant"
        val currentWeather = true
        val timeformat = "unixtime"
        val timezone = "GMT"
        val startDate = "2023-10-05"
        val endDate = "2023-10-12"
        Log.d("WeatherApp", "1 Latitude: $latitude, Longitude: $longitude")

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.fetchWeatherData(
                latitude,
                longitude,
                daily,
                currentWeather,
                timeformat,
                timezone,
                startDate,
                endDate
            )
        }


    }

    private fun updateUI(weatherData: WeatherData) {
        // Обновите текстовые поля с данными о погоде


        val currentWeatherUnits = weatherData.current_weather_units
        val currentWeather = weatherData.current_weather
        val dailyUnits = weatherData.daily_units
        val dailyWeather = weatherData.daily


        val currentWeatherTemperatureTextView = findViewById<TextView>(R.id.currentWeatherTemperatureTextView)
        currentWeatherTemperatureTextView.text = "${currentWeather.temperature}"

        val currentWeatherWindSpeedTextView = findViewById<TextView>(R.id.currentWeatherWindSpeedTextView)
        currentWeatherWindSpeedTextView.text = " ${currentWeather.windspeed}"

        val currentWeatherWindDirectionTextView = findViewById<TextView>(R.id.currentWeatherWindDirectionTextView)
        currentWeatherWindDirectionTextView.text = "${currentWeather.winddirection}"


    }




}
