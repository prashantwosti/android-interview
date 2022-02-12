package io.mx51.androidinterview.data.impl

import io.mx51.androidinterview.data.WeatherDetailsRepository
import io.mx51.androidinterview.data.model.UnitType
import io.mx51.androidinterview.data.model.WeatherDetails
import io.mx51.androidinterview.data.model.WeatherProvider
import io.mx51.androidinterview.data.retrofit.OpenWeatherMapService
import io.mx51.androidinterview.data.retrofit.WeatherStackService

class DefaultWeatherDetailsRepository(
    private val openWeatherMapService: OpenWeatherMapService,
    private val weatherStackService: WeatherStackService,
) : WeatherDetailsRepository {

    companion object {
        const val ERROR = "Unfortunately, that didn't work."
    }

    override suspend fun getWeatherDetails(unitType: UnitType): Result<WeatherDetails> = try {
        val response = openWeatherMapService.getCurrentWeather(
            getUnitTypeKey(
                WeatherProvider.OpenWeather,
                unitType
            )
        )
        when {
            response.isSuccessful -> {
                response.body()?.let {
                    Result.success(it.toWeatherDetails(unitType))
                } ?: kotlin.run {
                    fallbackToWeatherStack(unitType)
                }
            }
            else -> {
                fallbackToWeatherStack(unitType)
            }
        }
    } catch (exception: Exception) {
        fallbackToWeatherStack(unitType)
    }

    /**
     * WeatherStack api is to be called only when the primary open weather api fails.
     * */
    private suspend fun fallbackToWeatherStack(unitType: UnitType): Result<WeatherDetails> = try {
        val response = weatherStackService.getCurrentWeather(
            getUnitTypeKey(
                WeatherProvider.WeatherStack,
                unitType
            )
        )
        if (response.isSuccessful) {
            response.body()?.let {
                Result.success(it.toWeatherDetails(unitType))
            } ?: kotlin.run {
                Result.failure(Throwable(ERROR))
            }
        } else
            Result.failure(Throwable(ERROR))
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    private fun getUnitTypeKey(provider: WeatherProvider, type: UnitType): String {
        return when (type) {

            UnitType.Imperial -> {
                when (provider) {
                    WeatherProvider.OpenWeather -> "imperial"
                    WeatherProvider.WeatherStack -> "f"
                }
            }

            UnitType.Metric -> when (provider) {
                WeatherProvider.OpenWeather -> "metric"
                WeatherProvider.WeatherStack -> "m"
            }
        }
    }
}