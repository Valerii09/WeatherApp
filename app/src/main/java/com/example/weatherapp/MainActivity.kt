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
        val latitudeTextView = findViewById<TextView>(R.id.latitudeTextView)
        latitudeTextView.text = "Latitude: ${weatherData.latitude}"

        val longitudeTextView = findViewById<TextView>(R.id.longitudeTextView)
        longitudeTextView.text = "Longitude: ${weatherData.longitude}"

        val generationTimeTextView = findViewById<TextView>(R.id.generationTimeTextView)
        generationTimeTextView.text = "Generation Time (ms): ${weatherData.generationtime_ms}"

        val utcOffsetTextView = findViewById<TextView>(R.id.utcOffsetTextView)
        utcOffsetTextView.text = "UTC Offset (seconds): ${weatherData.utc_offset_seconds}"

        val timezoneTextView = findViewById<TextView>(R.id.timezoneTextView)
        timezoneTextView.text = "Timezone: ${weatherData.timezone}"

        val timezoneAbbreviationTextView = findViewById<TextView>(R.id.timezoneAbbreviationTextView)
        timezoneAbbreviationTextView.text = "Timezone Abbreviation: ${weatherData.timezone_abbreviation}"

        val elevationTextView = findViewById<TextView>(R.id.elevationTextView)
        elevationTextView.text = "Elevation: ${weatherData.elevation}"

        val currentWeatherUnits = weatherData.current_weather_units
        val currentWeather = weatherData.current_weather
        val dailyUnits = weatherData.daily_units
        val dailyWeather = weatherData.daily

        // Обновите поля для текущей погоды
        val currentWeatherTimeTextView = findViewById<TextView>(R.id.currentWeatherTimeTextView)
        currentWeatherTimeTextView.text = "Current Weather Time: ${currentWeather.time}"

        val currentWeatherTemperatureTextView = findViewById<TextView>(R.id.currentWeatherTemperatureTextView)
        currentWeatherTemperatureTextView.text = "Current Weather Temperature: ${currentWeather.temperature}${currentWeatherUnits.temperature}"

        val currentWeatherWindSpeedTextView = findViewById<TextView>(R.id.currentWeatherWindSpeedTextView)
        currentWeatherWindSpeedTextView.text = "Current Weather Wind Speed: ${currentWeather.windspeed}${currentWeatherUnits.windspeed}"

        val currentWeatherWindDirectionTextView = findViewById<TextView>(R.id.currentWeatherWindDirectionTextView)
        currentWeatherWindDirectionTextView.text = "Current Weather Wind Direction: ${currentWeather.winddirection}${currentWeatherUnits.winddirection}"

        // Обновите поля для ежедневной погоды
        // Здесь вы можете использовать цикл или другой способ для отображения списка данных.

        // Добавьте логи, чтобы убедиться, что данные были установлены
        Log.d("WeatherApp", "Latitude: ${weatherData.latitude}")
        Log.d("WeatherApp", "Longitude: ${weatherData.longitude}")
        Log.d("WeatherApp", "Generation Time (ms): ${weatherData.generationtime_ms}")
        Log.d("WeatherApp", "UTC Offset (seconds): ${weatherData.utc_offset_seconds}")
        Log.d("WeatherApp", "Timezone: ${weatherData.timezone}")
        Log.d("WeatherApp", "Timezone Abbreviation: ${weatherData.timezone_abbreviation}")
        Log.d("WeatherApp", "Elevation: ${weatherData.elevation}")
        Log.d("WeatherApp", "Current Weather Time: ${currentWeather.time}")
        Log.d("WeatherApp", "Current Weather Temperature: ${currentWeather.temperature}${currentWeatherUnits.temperature}")
        Log.d("WeatherApp", "Current Weather Wind Speed: ${currentWeather.windspeed}${currentWeatherUnits.windspeed}")
        Log.d("WeatherApp", "Current Weather Wind Direction: ${currentWeather.winddirection}${currentWeatherUnits.winddirection}")
    }




}
