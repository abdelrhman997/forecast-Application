package com.example.daily.forecast.ui.home

import android.app.AlertDialog
import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.daily.forecast.R
import com.example.daily.forecast.data.CityModel
import com.example.daily.forecast.data.DailyForecast
import com.example.daily.forecast.data.ForecastResponse
import com.example.daily.forecast.data.OpenWeatherResponse
import com.example.daily.forecast.repository.MainRepository
import com.example.daily.forecast.ui.CityAdapter
import com.example.daily.forecast.ui.CityInterface
import com.example.daily.forecast.utils.Utilities.showToast

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val cityObservable = ObservableField<CityModel>()

    private val days = arrayListOf<DailyForecast>()
    val dayForecastAdapter = DayForecastAdapter(days)

    val searchClick = MutableLiveData<Boolean>()



    private fun selectCity(city: CityModel) {
        cityObservable.set(city)
        getData(city.name)
    }



    fun getData(city: String = "Cairo, EG", saveCity: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = MainRepository.getDailyForecast(getApplication(), city)
            withContext(Dispatchers.Main) { bindResponse(response, saveCity) }
        }
    }

    private fun bindResponse(response: OpenWeatherResponse, saveCity: Boolean) {
        when (response) {
            is OpenWeatherResponse.ErrorResponse ->
                Toast.makeText(getApplication(), response.message, Toast.LENGTH_LONG).show()
            is OpenWeatherResponse.ExceptionResponse ->
                Toast.makeText(getApplication(), response.message, Toast.LENGTH_LONG).show()
            is OpenWeatherResponse.DataResponse<*> -> {
                (response.data as? ForecastResponse)?.run {
                    cityObservable.set(city)
                    days.clear()
                    days.addAll(list)
                    dayForecastAdapter.notifyDataSetChanged()
                    cacheData()

                }
            }
        }
    }

    fun selectCity(view: View) {
        val builder = AlertDialog.Builder(view.context, R.style.MyAlertDialogTheme)
        builder.setView(R.layout.dialog_favourite_city)

        val alertDialog = builder.create()

        viewModelScope.launch(Dispatchers.IO) {
            val cities = MainRepository.getCities(view.context)
            withContext(Dispatchers.Main) {
                val citiesRecycler = alertDialog.findViewById<RecyclerView>(R.id.cities_recycler)
                citiesRecycler.adapter = CityAdapter(cities, object : CityInterface {
                    override fun onCityClick(city: CityModel) {
                        selectCity(city)
                        alertDialog.dismiss()
                    }
                })
            }
        }

        alertDialog.show()
    }


    fun search(view: View) {
        searchClick.postValue(true)
    }

     fun cacheData() {
        viewModelScope.launch(Dispatchers.IO) {
            days.forEach {
                it.cityName = cityObservable.get()!!.name
                MainRepository.saveDayForecast(getApplication(), it)
            }
        }
    }
}