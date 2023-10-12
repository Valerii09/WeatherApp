package com.example.weatherapp.view

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private val PREFS_FILENAME = "weather_app_prefs"

    // Имя ключа для сохранения и извлечения значения
    private val KEY_CITY = "city"
    private val KEY_CONTINENT = "continent"
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // Скрывает статус-бар
        // Скрыть статус-бар и не показывать его при потягивании шторки
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val prefs: SharedPreferences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val savedDateTime = prefs.getString(KEY_CITY, null)


        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        // Наблюдайте за LiveData из ВьюМодели и обновляйте представление при изменении данных


        // Замените значения широты и долготы на нужные
        val latitude = 55.0
        val longitude = 82.875
        val apiKey = "74723a40510345568e67145a7679f85c" // Замените на свой ключ API

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
        viewModel.getWeatherData().observe(this, Observer { weatherData ->
            updateUI(weatherData)
        })

    }

    private fun updateUI(weatherData: WeatherData) {
        val prefs = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val city = prefs.getString(KEY_CITY, "")
        val continent = prefs.getString(KEY_CONTINENT, "")
        val timeZone = TimeZone.getTimeZone("$continent/$city")


        val currentWeather = weatherData.current_weather

        val tvDateTime = findViewById<TextView>(R.id.tv_date_time)

        // Получите текущую дату и время
        val currentDateTime = Calendar.getInstance().time

        // Форматируйте дату и время в желаемый формат (24-часовой формат)
        // Создайте SimpleDateFormat с указанием нужного формата и локали
        val dateFormat = SimpleDateFormat("dd MMMM, yyyy HH:mm", Locale.getDefault())

// Установите временную зону в SimpleDateFormat
        dateFormat.timeZone = timeZone

// Теперь, когда вы вызываете format, время будет отображаться с учетом установленной временной зоны
        val formattedDateTime = dateFormat.format(currentDateTime)

        // Установите отформатированную дату и время в TextView
        tvDateTime.text = formattedDateTime




        val currentWeatherTemperatureTextView =
            findViewById<TextView>(R.id.currentWeatherTemperatureTextView)
        val temperature = currentWeather.temperature
        currentWeatherTemperatureTextView.text = "$temperature"
        Log.d("WeatherApp", "Temperature: $temperature")

        val currentWeatherWindSpeedTextView =
            findViewById<TextView>(R.id.currentWeatherWindSpeedTextView)
        val windSpeed = currentWeather.windspeed
        currentWeatherWindSpeedTextView.text = "Wind speed\n$windSpeed km/h"
        Log.d("WeatherApp", "Wind Speed: $windSpeed")

        val currentWeatherWindDirectionTextView =
            findViewById<TextView>(R.id.currentWeatherWindDirectionTextView)
        val windDirection = currentWeather.winddirection
        currentWeatherWindDirectionTextView.text = "Wind direction\n$windDirection°"
        Log.d("WeatherApp", "Wind Direction: $windDirection")

        val dailyWeather2 = weatherData.daily
        val firstDaySunsetTimestamp2 = dailyWeather2.sunrise[7]
        Log.d("WeatherApp", "Sunset time (timestamp): $firstDaySunsetTimestamp2")

        try {
            val sunsetTimeInSeconds = firstDaySunsetTimestamp2.toLong() // Преобразуем timestamp в Long

            // Устанавливаем часовой пояс "Asia/Novosibirsk" (GMT+7)


            // Форматтер для форматирования времени в часовой пояс "Asia/Novosibirsk"
            val localFormatter = SimpleDateFormat("HH:mm")
            localFormatter.timeZone = timeZone

            // Преобразуем timestamp из секунд в миллисекунды с начала эпохи
            val sunsetTimeMillis = sunsetTimeInSeconds * 1000

            // Получаем объект Date из timestamp
            val sunsetDate = Date(sunsetTimeMillis)

            // Форматируем время и выводим его
            val localTime = localFormatter.format(sunsetDate)

            val sunsetTextView2 = findViewById<TextView>(R.id.tv_sunrise_time)
            sunsetTextView2.text = "Sunrise\n$localTime"
            Log.d("WeatherApp", "Sunset time: $localTime")
        } catch (e: ParseException) {
            Log.d("WeatherApp", "Error parsing sunset time: ${e.message}")
        }

        val dailyWeather = weatherData.daily
        val firstDaySunsetTimestamp = dailyWeather.sunset[7]
        Log.d("WeatherApp", "Sunset time (timestamp): $firstDaySunsetTimestamp")

        try {
            val sunsetTimeInSeconds = firstDaySunsetTimestamp.toLong() // Преобразуем timestamp в Long

            // Устанавливаем часовой пояс "Asia/Novosibirsk" (GMT+7)


            // Форматтер для форматирования времени в часовой пояс "Asia/Novosibirsk"
            val localFormatter = SimpleDateFormat("HH:mm")
            localFormatter.timeZone = timeZone

            // Преобразуем timestamp из секунд в миллисекунды с начала эпохи
            val sunsetTimeMillis = sunsetTimeInSeconds * 1000

            // Получаем объект Date из timestamp
            val sunsetDate = Date(sunsetTimeMillis)

            // Форматируем время и выводим его
            val localTime = localFormatter.format(sunsetDate)

            val sunsetTextView = findViewById<TextView>(R.id.tv_sunset_time)
            sunsetTextView.text = "Sunset\n$localTime"
            Log.d("WeatherApp", "Sunset time: $localTime")
        } catch (e: ParseException) {
            Log.d("WeatherApp", "Error parsing sunset time: ${e.message}")
        }



        val uv_index = dailyWeather.uv_index_max[7]
        Log.d("WeatherApp", "uv_index: ${uv_index}")
        val uv_indexTextView = findViewById<TextView>(R.id.uv_index)
        uv_indexTextView.text = "UV index\n$uv_index"

        val rain = dailyWeather.rain_sum[7]
        Log.d("WeatherApp", "uv_index: ${rain}")
        val rainTextView = findViewById<TextView>(R.id.rain_sum)
        rainTextView.text = "Rain sum\n$rain"

        val weathercode = weatherData.current_weather.weathercode
        Log.d("WeatherApp", "weathercode: ${weathercode}")
        val weatherCondition = findViewById<TextView>(R.id.tv_weather_condition)
        weatherCondition.text = Util.getWeatherInfo(weathercode)


    }
    }
