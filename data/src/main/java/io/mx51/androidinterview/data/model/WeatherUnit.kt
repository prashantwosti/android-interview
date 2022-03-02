package io.mx51.androidinterview.data.model

sealed class WeatherProvider {
    object OpenWeather : WeatherProvider()
    object WeatherStack : WeatherProvider()
}

sealed class UnitType {
    object Imperial : UnitType()
    object Metric : UnitType()
}