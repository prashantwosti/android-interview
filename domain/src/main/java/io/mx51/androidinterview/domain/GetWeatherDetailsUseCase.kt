package io.mx51.androidinterview.domain

import io.mx51.androidinterview.data.WeatherDetailsRepository
import io.mx51.androidinterview.data.model.UnitType
import io.mx51.androidinterview.data.model.WeatherDetails

class GetWeatherDetailsUseCase(
    private val weatherDetailsRepository: WeatherDetailsRepository
) : UseCase<UnitType, Result<WeatherDetails>>() {

    override suspend fun run(params: UnitType): Result<WeatherDetails> {
        return weatherDetailsRepository.getWeatherDetails(params)
    }

}
