package com.example.weatherapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Repository.WeatherRepository
import com.example.weatherapp.model.WeatherData
import kotlinx.coroutines.launch


class WeatherViewModel : ViewModel() {
    private val weatherData = MutableLiveData<WeatherData>()
    private val repository = WeatherRepository()

    fun getWeatherData(): LiveData<WeatherData> {
        // Возвращает LiveData для наблюдения из активности или фрагмента
        return weatherData
    }

    suspend fun fetchWeatherData(
        latitude: Double,
        longitude: Double,
        daily: String,
        currentWeather: Boolean,
        timeformat: String,
        timezone: String,
        startDate: String,
        endDate: String
    ) {
        viewModelScope.launch {
            try {
                val data = repository.fetchWeatherData(
                    latitude,
                    longitude,
                    daily,
                    currentWeather,
                    timeformat,
                    timezone,
                    startDate,
                    endDate
                )
                weatherData.value = data
            } catch (e: Exception) {
                // Обработка ошибок при запросе к API
            }
        }
    }


}
