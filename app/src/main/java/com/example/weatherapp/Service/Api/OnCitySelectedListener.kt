package com.example.weatherapp.Service.Api

interface OnCitySelectedListener {
    fun onCitySelected(cityName: String)
    fun onCoordinatesReceived(latitude: Double, longitude: Double)
}