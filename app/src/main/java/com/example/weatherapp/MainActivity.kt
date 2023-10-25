package com.example.weatherapp

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.Repository.WeatherRepository
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.example.weatherapp.ViewModel.WeatherViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.TextView
import com.example.weatherapp.data.WeatherData
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val repository = WeatherRepository()
        viewModel = ViewModelProvider(this, WeatherViewModelFactory(repository))
            .get(WeatherViewModel::class.java)

        val latitude = 55.0415
        val longitude = 82.9346

        val apiKey =
            "026e9642a6d0a86dc1e7bed4faa83fba"
        // Показать фрагмент с прогресс баром
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressBar.isIndeterminate = true // Запустить анимацию вращения

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val weatherData = viewModel.fetchWeatherData(latitude, longitude, apiKey)


                runOnUiThread {

                    updateUI(weatherData)
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                progressBar.visibility = View.GONE

            }
        }
    }

    private fun updateUI(weatherData: WeatherData) {
        val timezoneSeconds = weatherData.timezone
        val hours = timezoneSeconds / 3600
        val zoneId = ZoneId.of("GMT" + (if (hours >= 0) "+" else "") + hours)
        val currentTime = ZonedDateTime.now(zoneId)
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy h:mm a", Locale.ENGLISH)
        val formattedDateTime = currentTime.format(dateTimeFormatter)
        val tvDateTime = findViewById<TextView>(R.id.tv_date_time)
        tvDateTime.text = formattedDateTime


        val cityTextView = findViewById<TextView>(R.id.cityTextView)
        val currentWeatherTemperatureTextView =
            findViewById<TextView>(R.id.currentWeatherTemperatureTextView)
        val currentWeatherHumidityTextView =
            findViewById<TextView>(R.id.currentWeatherHumidityTextView)
        val currentWeatherSunriseTextView =
            findViewById<TextView>(R.id.currentWeatherSunriseTextView)
        val currentWeatherSunsetTextView = findViewById<TextView>(R.id.currentWeatherSunsetTextView)
        val currentWeatherUVIndexTextView =
            findViewById<TextView>(R.id.currentWeatherUVIndexTextView)

        val currentWeatherWindSpeedTextView =
            findViewById<TextView>(R.id.currentWeatherWindSpeedTextView)
        val currentWeatherWindDirectionTextView =
            findViewById<TextView>(R.id.currentWeatherWindDirectionTextView)
        val tvWeatherCondition = findViewById<TextView>(R.id.tv_weather_condition)



        currentWeatherWindSpeedTextView.text = "Wind speed\n${weatherData.wind.speed} m/s"
        currentWeatherWindDirectionTextView.text = "Wind direction\n${weatherData.wind.deg}°"
        tvWeatherCondition.text = weatherData.weather[0].description

        // Преобразование Кельвинов в градусы Цельсия
        val temperatureCelsius = (weatherData.main.temp - 273.15).roundToInt()
        currentWeatherTemperatureTextView.text =
            "$temperatureCelsius°C"

        // Отображение влажности
        val humidity = weatherData.main.humidity
        currentWeatherHumidityTextView.text = "Humidity\n$humidity%"

        // Отображение времени восхода и заката
        val sunriseTime = SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(Date((weatherData.sys.sunrise + weatherData.timezone) * 1000))
        val sunsetTime = SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(Date((weatherData.sys.sunset + weatherData.timezone) * 1000))
        currentWeatherSunriseTextView.text = "Sunrise\n$sunriseTime"
        currentWeatherSunsetTextView.text = "Sunset\n$sunsetTime"

        val pressure = (weatherData.main.pressure * 0.75).roundToInt()
        currentWeatherUVIndexTextView.text = "Atm pressure\n$pressure mmHg"

        cityTextView.text = weatherData.name
    }
}



