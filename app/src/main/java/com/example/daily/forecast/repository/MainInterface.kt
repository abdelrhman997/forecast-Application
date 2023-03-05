package com.example.daily.forecast.repository

import android.content.Context
import com.example.daily.forecast.data.CityModel
import com.example.daily.forecast.data.DailyForecast
import com.example.daily.forecast.data.OpenWeatherResponse

interface MainInterface {

    suspend fun getDailyForecast(
        context: Context, city: String, nDays: Int = 5
    ): OpenWeatherResponse

    suspend fun saveCity(context: Context, city: CityModel): OpenWeatherResponse
    suspend fun getCities(context: Context): List<CityModel>
    suspend fun ifCityExists(context: Context, name: String): Boolean
    suspend fun getCount(context: Context): Int

    suspend fun saveDayForecast(context: Context, forecast: DailyForecast): OpenWeatherResponse
    suspend fun readForecastForCity(context: Context, cityName: String): OpenWeatherResponse
}