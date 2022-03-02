package io.mx51.androidinterview.data.model

data class WeatherDetails(
    val temperature: Double = 0.0,
    val windSpeed: Double = 0.0,
    val description: String = "",
    val locationName: String = "",
    val unitType: UnitType? = null
)