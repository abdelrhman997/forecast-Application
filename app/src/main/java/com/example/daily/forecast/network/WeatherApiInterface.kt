package com.example.daily.forecast.network

import com.example.daily.forecast.BuildConfig
import com.example.daily.forecast.data.CityResponse
import com.example.daily.forecast.data.ForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiInterface {


    @GET("forecast/daily")
    suspend fun dailyForecast(
        @Query("q") city: String,
        @Query("cnt") count: Int = 5,
        @Query("units") units: String = "metric",
        @Query("appid") appId: String = BuildConfig.OPEN_WEATHER_API_KEY
    ): Response<ForecastResponse>

    @GET("find")
    suspend fun cityList(
        @Query("q") city: String,
        @Query("cnt") count: Int = 20,
        @Query("appid") appId: String = BuildConfig.OPEN_WEATHER_API_KEY
    ): Response<CityResponse>
}