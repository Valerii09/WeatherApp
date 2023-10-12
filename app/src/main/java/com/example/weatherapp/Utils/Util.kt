package com.example.weatherapp.Utils

import java.text.SimpleDateFormat
import java.util.Locale

object Util {

    fun getWeatherInfo(code:Int):String{
        return  when(code){
            0 -> "Clear sky"
            1 -> "Mainly clear"
            2 -> "partly cloudy"
            3 -> "overcast"
            45, 48 -> "Fog"
            51, 53, 55,
            -> "Drizzle"
            56, 57 -> "Freezing Drizzle"
            61,
            -> "Rain: Slight"
            63 -> "Rain: Moderate"
            65 -> "Rain: Heavy"
            66, 67 -> "Freezing Rain"
            71 -> "Snow fall: Slight"
            73 -> "Snow fall: moderate"
            75 -> "Snow fall: Heavy"
            77 -> "Snow grains"
            80, 81, 82 -> "Rain showers: Slight"
            85, 86 -> "Snow showers slight"
            95, 96, 99 -> "Thunderstorm: Slight"
            else -> "Unknown"
        }
    }


}