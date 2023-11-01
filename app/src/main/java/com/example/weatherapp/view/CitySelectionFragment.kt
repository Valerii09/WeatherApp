package com.example.weatherapp.view

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
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
    private var coordinatesReceived: Boolean = false
    private lateinit var coordinates: Pair<Double, Double>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_city_selection, container, false)

        val cityEditText = rootView.findViewById<EditText>(R.id.cityEditText)
        val countryEditText = rootView.findViewById<EditText>(R.id.countryEditText)
        val confirmButton = rootView.findViewById<Button>(R.id.confirmButton)

        openCageRepository = OpenCageRepository()

        confirmButton.setOnClickListener {
            val selectedCity = cityEditText.text.toString()
            val selectedCountry = countryEditText.text.toString()
            lifecycleScope.launch {
                val result = openCageRepository.geocodeCity(selectedCity, selectedCountry)

                if (result != null) {
                    coordinates = result
                    citySelectedListener?.onCoordinatesReceived(coordinates.first, coordinates.second)

                    Log.d("CitySelectionFragment", "Coordinates: $coordinates")
                    // Координаты установлены, не передавать сразу, а дождаться закрытия фрагмента
                    dismiss()
                } else {
                    // Обработка ошибки геокодирования
                    Toast.makeText(requireContext(), "Ошибка геокодирования", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return rootView
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (coordinatesReceived) {
            citySelectedListener?.onCoordinatesReceived(coordinates.first, coordinates.second)
        }
    }
}

