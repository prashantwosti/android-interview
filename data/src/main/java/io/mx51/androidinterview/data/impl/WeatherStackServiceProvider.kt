package io.mx51.androidinterview.data.impl

import io.mx51.androidinterview.data.retrofit.WeatherStackService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherStackServiceProvider {
    fun provideWeatherStackService(): WeatherStackService {
        return Retrofit.Builder()
            .baseUrl("http://api.weatherstack.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherStackService::class.java)
    }
}