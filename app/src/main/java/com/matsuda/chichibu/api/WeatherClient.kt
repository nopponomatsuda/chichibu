package com.matsuda.chichibu.api

import com.google.gson.GsonBuilder
import com.matsuda.chichibu.api.service.IWeatherService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//TODO
object WeatherClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private var retrofit: Retrofit? = null
    private val weatherService: IWeatherService? = create(IWeatherService::class.java)

    init {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()

                val request = original.newBuilder()
                    .header("Accept", "application/json")
                    .method(original.method(), original.body())
                    .build()

                return@Interceptor chain.proceed(request)
            })
            .readTimeout(30, TimeUnit.SECONDS)

        // log interceptor
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(loggingInterceptor)

        val gson = GsonBuilder()
            .serializeNulls()
            .create()

        // create retrofit
        retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .build()
    }

    private fun <S> create(serviceClass: Class<S>): S? {
        return retrofit?.create(serviceClass)
    }


    fun getWeather(place: String) {
        val weatherService = weatherService ?: return
        weatherService.getWeather(place).execute()
    }
}