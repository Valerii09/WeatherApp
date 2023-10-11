package com.example.weatherapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.WeatherData

class WeatherViewModel : ViewModel() {
    private val weatherData = MutableLiveData<WeatherData>()

    fun getWeatherData(): LiveData<WeatherData> {
        // Здесь можно запросить погодные данные из какого-либо источника и обновить LiveData
        return weatherData
    }
}
