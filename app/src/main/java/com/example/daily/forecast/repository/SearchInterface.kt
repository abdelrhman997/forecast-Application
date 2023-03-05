package com.example.daily.forecast.repository

import com.example.daily.forecast.data.OpenWeatherResponse

interface SearchInterface {

    suspend fun getCities(city: String): OpenWeatherResponse
}