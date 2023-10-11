package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.example.weatherapp.model.WeatherData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        // Наблюдайте за LiveData из ВьюМодели и обновляйте представление при изменении данных
        viewModel.getWeatherData().observe(this, Observer { weatherData ->
            updateUI(weatherData)
        })

        // Замените значения широты и долготы на нужные
        val latitude = 55.0
        val longitude = 82.875
        val daily = "weathercode,temperature_2m_max,apparent_temperature_max,windspeed_10m_max,winddirection_10m_dominant"
        val currentWeather = true
        val timeformat = "unixtime"
        val timezone = "GMT"
        val startDate = "2023-10-05"
        val endDate = "2023-10-12"

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
        val cityTextView = findViewById<TextView>(R.id.cityTextView)
        cityTextView.text = "City: ${weatherData.city}"

        val temperatureTextView = findViewById<TextView>(R.id.temperatureTextView)
        temperatureTextView.text = "Temperature: ${weatherData.temperature}°C"

        val conditionTextView = findViewById<TextView>(R.id.conditionTextView)
        conditionTextView.text = "Condition: ${weatherData.condition}"
    }
}
