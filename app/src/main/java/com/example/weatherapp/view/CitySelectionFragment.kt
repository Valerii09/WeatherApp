package com.example.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.weatherapp.R
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.Repository.OpenCageRepository
import com.example.weatherapp.Service.Api.OnCitySelectedListener
import kotlinx.coroutines.launch

class CitySelectionFragment : DialogFragment() {
    var citySelectedListener: OnCitySelectedListener? = null
    private lateinit var openCageRepository: OpenCageRepository



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_city_selection, container, false)

        val cityEditText = rootView.findViewById<EditText>(R.id.cityEditText)
        val confirmButton = rootView.findViewById<Button>(R.id.confirmButton)

        openCageRepository = OpenCageRepository()

        confirmButton.setOnClickListener {
            val selectedCity = cityEditText.text.toString()

            // Запускаем сопрограмму для выполнения функции geocodeCity
            lifecycleScope.launch {
                val coordinates = openCageRepository.geocodeCity(selectedCity)

                if (coordinates != null) {
                    citySelectedListener?.onCoordinatesReceived(coordinates.first, coordinates.second)
                    dismiss()
                } else {
                    // Обработка ошибки геокодирования
                    Toast.makeText(requireContext(), "Ошибка геокодирования", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return rootView
    }
}


