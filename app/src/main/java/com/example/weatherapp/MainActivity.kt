package com.example.weatherapp

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
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
import android.widget.Toast
import com.example.weatherapp.data.WeatherData
import com.example.weatherapp.view.CitySelectionFragment
import androidx.fragment.app.FragmentManager
import com.example.weatherapp.Service.Api.OnCitySelectedListener
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), OnCitySelectedListener {
    private lateinit var viewModel: WeatherViewModel
    private var newLatitude: Double = 0.0
    private var newLongitude: Double = 0.0
    private var isFragmentClosed: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val errorContainer = findViewById<FrameLayout>(R.id.errorContainer)
        val noInternetFragment = findViewById<FrameLayout>(R.id.noInternet)
        val repository = WeatherRepository()

        viewModel = ViewModelProvider(this, WeatherViewModelFactory(repository))
            .get(WeatherViewModel::class.java)
        val citySelectionFragment = CitySelectionFragment()
        citySelectionFragment.citySelectedListener = this // Устанавливаем текущую активность как слушателя
        citySelectionFragment.show(supportFragmentManager, "CitySelectionFragment")

        val apiKey = "026e9642a6d0a86dc1e7bed4faa83fba"
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressBar.isIndeterminate = true

        if (isInternetAvailable(this)) {

            // Запрос данных о погоде для заданных координат
            val latitude = 55.0415
            val longitude = 82.9346

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val weatherData = viewModel.fetchWeatherData(latitude, longitude, apiKey)

                    runOnUiThread {
                        updateUI(weatherData)
                        progressBar.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    errorContainer.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
        } else {
            noInternetFragment.visibility = View.VISIBLE
        }

    }

    // Реализация интерфейса для обработки выбора города и обновления данных о погоде
    override fun onCitySelected(cityName: String) {
        val citySelectionFragment = CitySelectionFragment()
        citySelectionFragment.citySelectedListener = this
        citySelectionFragment.show(supportFragmentManager, "CitySelectionFragment")

    }

    override fun onCoordinatesReceived(latitude: Double, longitude: Double) {
        Log.d("MainActivity", "Received coordinates: $latitude, $longitude")
        newLatitude = latitude
        newLongitude = longitude
        isFragmentClosed = true

        if (isFragmentClosed) {
            updateWeatherData(newLatitude, newLongitude)
        }
    }

    private fun updateUI(weatherData: WeatherData) {
        // Получаем смещение временной зоны и создаем объект ZoneId
        val timezoneSeconds = weatherData.timezone
        val hours = timezoneSeconds / 3600
        val zoneId = ZoneId.of("GMT" + (if (hours >= 0) "+" else "") + hours)

        // Получаем текущее время с учетом временной зоны
        val currentTime = ZonedDateTime.now(zoneId)

        // Форматируем текущее время в удобный формат
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy h:mm a", Locale.ENGLISH)
        val formattedDateTime = currentTime.format(dateTimeFormatter)

        // Находим и устанавливаем текст в TextView для даты и времени
        val tvDateTime = findViewById<TextView>(R.id.tv_date_time)
        tvDateTime.text = formattedDateTime

        // Находим и устанавливаем текст в TextView для города
        val cityTextView = findViewById<TextView>(R.id.cityTextView)
        cityTextView.setOnClickListener {
            val citySelectionFragment = CitySelectionFragment()
            citySelectionFragment.show(supportFragmentManager, "CitySelectionFragment")
        }

        cityTextView.text = weatherData.name

        // Остальные TextView
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

        // Отображение информации о погоде
        currentWeatherWindSpeedTextView.text = "Wind speed\n${weatherData.wind.speed} m/s"
        currentWeatherWindDirectionTextView.text = "Wind direction\n${weatherData.wind.deg}°"
        tvWeatherCondition.text = weatherData.weather[0].description

    // Преобразование Кельвинов в градусы Цельсия
        val temperatureCelsius = (weatherData.main.temp - 273.15).roundToInt()
        currentWeatherTemperatureTextView.text = "$temperatureCelsius°C"

    // Отображение влажности
        val humidity = weatherData.main.humidity
        currentWeatherHumidityTextView.text = "Humidity\n$humidity%"

    // Отображение времени восхода и заката
        val sunriseTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
            Date((weatherData.sys.sunrise + weatherData.timezone) * 1000)
        )
        val sunsetTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
            Date((weatherData.sys.sunset + weatherData.timezone) * 1000)
        )
        currentWeatherSunriseTextView.text = "Sunrise\n$sunriseTime"
        currentWeatherSunsetTextView.text = "Sunset\n$sunsetTime"

    // Отображение давления
        val pressure = (weatherData.main.pressure * 0.75).roundToInt()
        currentWeatherUVIndexTextView.text = "Atm pressure\n$pressure mmHg"

    }

    // Функция для проверки наличия интернет-соединения
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }


    private fun updateWeatherData(latitude: Double, longitude: Double) {
        val errorContainer = findViewById<FrameLayout>(R.id.errorContainer)
        val noInternetFragment = findViewById<FrameLayout>(R.id.noInternet)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        Log.d("updateWeatherData", "Coordinates: $latitude")
        // Показать фрагмент с прогресс баром
        progressBar.visibility = View.VISIBLE
        progressBar.isIndeterminate = true // Запустить анимацию вращения

        val apiKey = "026e9642a6d0a86dc1e7bed4faa83fba"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val weatherData = viewModel.fetchWeatherData(latitude, longitude, apiKey)

                runOnUiThread {
                    updateUI(weatherData)
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorContainer.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
    }



}





