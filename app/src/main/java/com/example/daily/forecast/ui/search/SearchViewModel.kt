package com.example.daily.forecast.ui.search

import android.content.Context
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.daily.forecast.data.CityModel
import com.example.daily.forecast.data.CityResponse
import com.example.daily.forecast.data.OpenWeatherResponse
import com.example.daily.forecast.repository.SearchRepository
import com.example.daily.forecast.ui.CityAdapter
import com.example.daily.forecast.ui.CityInterface
import com.example.daily.forecast.utils.SingleLiveEvent
import com.example.daily.forecast.utils.Utilities

class SearchViewModel : ViewModel() {

    val searchObservable = ObservableField<String>()

    val searchAction = TextView.OnEditorActionListener { v, actionId, event ->
        when (actionId) {
            EditorInfo.IME_ACTION_SEARCH -> {
                Utilities.hideKeyboard(v)

                search(v.context, query = searchObservable.get()?.trim() ?: "")

                true
            }
            else -> false
        }
    }

    private val cities = arrayListOf<CityModel>()
    val cityAdapter = CityAdapter(cities, object : CityInterface {
        override fun onCityClick(city: CityModel) {
            selectCity.postValue(city)
        }
    })

    val onBackEvent = SingleLiveEvent<Boolean>()
    val selectCity = SingleLiveEvent<CityModel>()

    val onBackClicked: () -> Unit = { onBackEvent.postValue(true) }

    fun search(context: Context, query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = SearchRepository.getCities(query)
            withContext(Dispatchers.Main) {
                when (response) {
                    is OpenWeatherResponse.ErrorResponse ->
                        Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                    is OpenWeatherResponse.ExceptionResponse ->
                        Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                    is OpenWeatherResponse.DataResponse<*> -> {
                        (response.data as? CityResponse)?.run {
                            cities.clear()
                            cities.addAll(list)
                            cityAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}