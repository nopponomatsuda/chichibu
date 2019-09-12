package com.matsuda.chichibu.api.service

import com.matsuda.chichibu.data.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherService {
    @GET("weather")
    fun getWeather(@Query("q") q: String): Call<Weather>
}