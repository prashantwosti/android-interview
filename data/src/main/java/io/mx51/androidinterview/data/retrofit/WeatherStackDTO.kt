import com.google.gson.annotations.SerializedName
import io.mx51.androidinterview.data.model.UnitType
import io.mx51.androidinterview.data.model.WeatherDetails

data class WeatherStackDTO(
    val request: Request,
    val location: Location,
    val current: Current
) {
    data class Current(
        @SerializedName("observation_time")
        val observationTime: String,

        val temperature: Double,

        @SerializedName("weather_code")
        val weatherCode: Long,

        @SerializedName("weather_icons")
        val weatherIcons: List<String>,

        @SerializedName("weather_descriptions")
        val weatherDescriptions: List<String>,

        @SerializedName("wind_speed")
        val windSpeed: Double,

        @SerializedName("wind_degree")
        val windDegree: Long,

        @SerializedName("wind_dir")
        val windDir: String,

        val pressure: Long,
        val precip: Long,
        val humidity: Long,
        val cloudcover: Long,
        val feelslike: Long,

        @SerializedName("uv_index")
        val uvIndex: Long,

        val visibility: Long,

        @SerializedName("is_day")
        val isDay: String
    )

    data class Location(
        val name: String,
        val country: String,
        val region: String,
        val lat: String,
        val lon: String,

        @SerializedName("timezone_id")
        val timezoneID: String,

        val localtime: String,

        @SerializedName("localtime_epoch")
        val localtimeEpoch: Long,

        @SerializedName("utc_offset")
        val utcOffset: String
    )

    data class Request(
        val type: String,
        val query: String,
        val language: String,
        val unit: String
    )

    fun toWeatherDetails(unitType: UnitType) = WeatherDetails(
        temperature = current.temperature,
        windSpeed = when (unitType) {
            UnitType.Imperial -> {
                // miles per hour to meter per seconds
                current.windSpeed * 0.44704
            }
            else -> {
                // kilometers per hour to meter per seconds
                current.windSpeed * 0.27778
            }
        },
        description = current.weatherDescriptions.firstOrNull().orEmpty(),
        locationName = location.name
    )

}
