package io.mx51.androidinterview.data.retrofit

import WeatherStackDTO
import io.mx51.androidinterview.data.model.UnitType
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherStackService {
    @GET("current?access_key=389a42a2cb702a26c886db9d0d2bc15a&query=sydney")
    suspend fun getCurrentWeather(@Query("units") units: String): Response<WeatherStackDTO>
}