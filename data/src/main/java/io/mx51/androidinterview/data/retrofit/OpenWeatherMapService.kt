package io.mx51.androidinterview.data.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapService {
    @GET("data/2.5/weather?q=sydney,AU&appid=2326504fb9b100bee21400190e4dbe6d")
    suspend fun getCurrentWeather(@Query("units") unit: String): Response<OpenWeatherMapDTO>
}