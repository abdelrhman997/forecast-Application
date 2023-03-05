package com.example.daily.forecast.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.daily.forecast.data.OpenWeatherResponse
import com.example.daily.forecast.network.MyRetrofitClient
import com.example.daily.forecast.network.WeatherApiInterface

object SearchRepository : SearchInterface {

    private var weatherApi: WeatherApiInterface =
        MyRetrofitClient.createService(WeatherApiInterface::class.java)

    override suspend fun getCities(city: String): OpenWeatherResponse {
        return try {
            val response = weatherApi.cityList(city)

            if (response.isSuccessful) {
                OpenWeatherResponse.DataResponse(response.body())
            } else {
                OpenWeatherResponse.ErrorResponse(response.code(), response.message())
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) { e.printStackTrace() }
            OpenWeatherResponse.ExceptionResponse(e.message)
        }
    }
}