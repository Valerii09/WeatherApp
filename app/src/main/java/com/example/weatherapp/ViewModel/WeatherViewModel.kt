package com.example.weatherapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.Repository.WeatherRepository
import com.example.weatherapp.data.WeatherData

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    // LiveData для хранения данных о погоде
    private val weatherDataLiveData = MutableLiveData<WeatherData>()

    // Метод для получения LiveData
    fun getWeatherDataLiveData(): LiveData<WeatherData> {
        return weatherDataLiveData
    }

    suspend fun fetchWeatherData(latitude: Double, longitude: Double, apiKey: String): WeatherData {
        try {
            val weatherData = repository.fetchWeatherData(latitude, longitude, apiKey)
            weatherDataLiveData.postValue(weatherData)
            return weatherData
        } catch (e: Exception) {
            throw e
        }
    }
}
