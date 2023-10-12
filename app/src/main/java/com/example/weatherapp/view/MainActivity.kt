package com.example.weatherapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.Service.Api.GeocodingService
import com.example.weatherapp.Utils.Util
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.example.weatherapp.model.WeatherData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val PREFS_FILENAME = "weather_app_prefs"

    // Имя ключа для сохранения и извлечения значения
    private val KEY_CITY = "city"
    private val KEY_CONTINENT = "continent"
    private lateinit var viewModel: WeatherViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Скрыть статус-бар и не показывать его при потягивании шторки
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val prefs: SharedPreferences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        val latitude = 55.0
        val longitude = 82.875
        val apiKey = "74723a40510345568e67145a7679f85c"
        val geocodingService = Retrofit.Builder().baseUrl("https://api.opencagedata.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(GeocodingService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = geocodingService.getCityByCoordinates("$latitude,$longitude", apiKey)
                if (response.isSuccessful) {
                    val geocodingData = response.body()
                    val city = geocodingData?.results?.firstOrNull()?.components?.city
                    val continent = geocodingData?.results?.firstOrNull()?.components?.continent

                    // Обновляем UI с полученным городом
                    runOnUiThread {
                        if (!city.isNullOrBlank()) {
                            val cityTextView = findViewById<TextView>(R.id.cityTextView)
                            cityTextView.text = "City: $city"
                            val editor = prefs.edit()
                            editor.putString(KEY_CITY, city)
                            editor.apply()
                        } else {
                            // Если город не найден
                        }
                        if (!continent.isNullOrBlank()) {
                            // Сохраняем континент в SharedPreferences
                            val editor = prefs.edit()
                            editor.putString(KEY_CONTINENT, continent)
                            editor.apply()
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

        val daily =
            "weathercode,temperature_2m_max,apparent_temperature_max,windspeed_10m_max,winddirection_10m_dominant,sunrise,sunset,uv_index_max,rain_sum"
        val currentWeather = true
        val timeformat = "unixtime"
        val timezone = "GMT"
        val startDate = "2023-10-05"
        val endDate = "2023-10-12"
        Log.d("WeatherApp", "1 Latitude: $latitude, Longitude: $longitude")

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.fetchWeatherData(
                latitude, longitude, daily, currentWeather, timeformat, timezone, startDate, endDate
            )
        }
        viewModel.getWeatherData().observe(this) { weatherData ->
            updateUI(weatherData)
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun updateUI(weatherData: WeatherData) {
        val prefs = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val city = prefs.getString(KEY_CITY, "")
        val continent = prefs.getString(KEY_CONTINENT, "")
        val timeZone = TimeZone.getTimeZone("$continent/$city")
        val currentWeather = weatherData.current_weather
        val dailyWeather = weatherData.daily

        //TEMPERATURE------------------------------------------------------------------
        val currentWeatherTemperatureTextView =
            findViewById<TextView>(R.id.currentWeatherTemperatureTextView)
        val temperature = currentWeather.temperature
        currentWeatherTemperatureTextView.text = "$temperature°С"
        Log.d("WeatherApp", "Temperature: $temperature")

        //WIND SPEED--------------------------------------------------------------------
        val currentWeatherWindSpeedTextView =
            findViewById<TextView>(R.id.currentWeatherWindSpeedTextView)
        val windSpeed = currentWeather.windspeed
        currentWeatherWindSpeedTextView.text = "Wind speed\n$windSpeed km/h"
        Log.d("WeatherApp", "Wind Speed: $windSpeed")

        //WIND DIRECTION----------------------------------------------------------------
        val currentWeatherWindDirectionTextView =
            findViewById<TextView>(R.id.currentWeatherWindDirectionTextView)
        val windDirection = currentWeather.winddirection
        currentWeatherWindDirectionTextView.text = "Wind direction\n$windDirection°"
        Log.d("WeatherApp", "Wind Direction: $windDirection")

        //WEATHER CODE------------------------------------------------------------------
        val weathercode = weatherData.current_weather.weathercode
        val weatherCondition = findViewById<TextView>(R.id.tv_weather_condition)
        weatherCondition.text = Util.getWeatherInfo(weathercode)
        Log.d("WeatherApp", "weathercode: ${weathercode}")

        //DATE AND TIME------------------------------------------------------------------
        val tvDateTime = findViewById<TextView>(R.id.tv_date_time)
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd MMMM, yyyy HH:mm", Locale.getDefault())
        dateFormat.timeZone = timeZone
        val formattedDateTime = dateFormat.format(currentDateTime)
        tvDateTime.text = formattedDateTime

        //SUNRISE-------------------------------------------------------------------------
        val firstDaySunsetTimestamp2 = dailyWeather.sunrise[7]
        Log.d("WeatherApp", "Sunset time (timestamp): $firstDaySunsetTimestamp2")
        val sunriseTimeInSeconds = firstDaySunsetTimestamp2
        // Устанавливаем часовой пояс "Asia/Novosibirsk" (GMT+7)
        // Форматтер для форматирования времени в часовой пояс "Asia/Novosibirsk"
        val localFormatterSunrise = SimpleDateFormat("HH:mm")
        localFormatterSunrise.timeZone = timeZone

        // Преобразуем timestamp из секунд в миллисекунды с начала эпохи
        val sunriseTimeMillis = sunriseTimeInSeconds * 1000

        // Форматируем время и выводим его
        val localTimeSunrire = localFormatterSunrise.format(Date(sunriseTimeMillis))
        val sunriseTextView = findViewById<TextView>(R.id.tv_sunrise_time)
        sunriseTextView.text = "Sunrise\n$localTimeSunrire"
        Log.d("WeatherApp", "Sunset time: $localTimeSunrire")

        //SUNSET------------------------------------------------------------------------
        val firstDaySunsetTimestamp = dailyWeather.sunset[7]
        Log.d("WeatherApp", "Sunset time (timestamp): $firstDaySunsetTimestamp")
        val sunsetTimeInSeconds = firstDaySunsetTimestamp

        // Устанавливаем часовой пояс "Asia/Novosibirsk" (GMT+7)
        // Форматтер для форматирования времени в часовой пояс "Asia/Novosibirsk"
        val localFormatterSunset = SimpleDateFormat("HH:mm")
        localFormatterSunset.timeZone = timeZone

        // Преобразуем timestamp из секунд в миллисекунды с начала эпохи
        val sunsetTimeMillis = sunsetTimeInSeconds * 1000

        // Получаем объект Date из timestamp
        val sunsetDate = Date(sunsetTimeMillis)

        // Форматируем время и выводим его
        val localTimeSunset = localFormatterSunset.format(sunsetDate)
        val sunsetTextView = findViewById<TextView>(R.id.tv_sunset_time)
        sunsetTextView.text = "Sunset\n$localTimeSunset"
        Log.d("WeatherApp", "Sunset time: $localTimeSunset")
        //----------------------------------------------------------------------------

        //UV INDEX--------------------------------------------------------------------
        val uv_index = dailyWeather.uv_index_max[7]
        Log.d("WeatherApp", "uv_index: ${uv_index}")
        val uv_indexTextView = findViewById<TextView>(R.id.uv_index)
        uv_indexTextView.text = "UV index\n$uv_index"

        //RAIN SUM--------------------------------------------------------------------
        val rain = dailyWeather.rain_sum[7]
        Log.d("WeatherApp", "uv_index: ${rain}")
        val rainTextView = findViewById<TextView>(R.id.rain_sum)
        rainTextView.text = "Rain sum\n$rain"
    }
}
