package io.mx51.androidinterview.data.impl

import com.google.gson.Gson
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mx51.androidinterview.data.model.UnitType
import io.mx51.androidinterview.data.retrofit.OpenWeatherMapDTO
import io.mx51.androidinterview.data.retrofit.OpenWeatherMapService
import io.mx51.androidinterview.data.retrofit.WeatherStackService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class DefaultWeatherDetailsRepositoryTest {

    companion object {
        const val DUMMY_OPEN_API_RESPONSE =
            "{\"coord\":{\"lon\":151.2073,\"lat\":-33.8679},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"base\":\"stations\",\"main\":{\"temp\":294.61,\"feels_like\":294.93,\"temp_min\":292.23,\"temp_max\":296.61,\"pressure\":1016,\"humidity\":81},\"visibility\":10000,\"wind\":{\"speed\":5.36,\"deg\":110,\"gust\":6.26},\"rain\":{\"1h\":0.12},\"clouds\":{\"all\":30},\"dt\":1644639522,\"sys\":{\"type\":2,\"id\":2018875,\"country\":\"AU\",\"sunrise\":1644607633,\"sunset\":1644655906},\"timezone\":39600,\"id\":2147714,\"name\":\"Sydney\",\"cod\":200}"
    }

    class Mocks {

        @MockK
        lateinit var openWeatherMapService: OpenWeatherMapService

        @MockK
        lateinit var weatherStackService: WeatherStackService

        init {
            MockKAnnotations.init(this, relaxUnitFun = true)
        }
    }

    lateinit var mocked: Mocks
    lateinit var repo: DefaultWeatherDetailsRepository

    @Before
    fun setup() {
        mocked = Mocks()
        repo = DefaultWeatherDetailsRepository(
            openWeatherMapService = mocked.openWeatherMapService,
            weatherStackService = mocked.weatherStackService
        )
    }

    @Test
    fun `GIVEN weather details repo WHEN get weather details is called with metric unit THEN call the open weather api by default`() =
        runTest {
            // setup
            val captures = mutableListOf<String>()

            // trigger
            repo.getWeatherDetails(UnitType.Metric)

            // verify
            coVerify(exactly = 1) { mocked.openWeatherMapService.getCurrentWeather(capture(captures)) }
        }

    @Test
    fun `GIVEN weather details repo WHEN get weather details is called with metric unit AND call to open weather is success THEN dont call the weather stack api`() =
        runTest {
            // setup
            val captures = mutableListOf<String>()
            val openWeatherResponse =
                Gson().fromJson(DUMMY_OPEN_API_RESPONSE, OpenWeatherMapDTO::class.java)
            coEvery { mocked.openWeatherMapService.getCurrentWeather(any()) } returns Response.success(
                openWeatherResponse
            )

            // trigger
            val response = repo.getWeatherDetails(UnitType.Metric)

            // verify
            coVerify(exactly = 1) { mocked.openWeatherMapService.getCurrentWeather(capture(captures)) }
            coVerify(exactly = 0) { mocked.weatherStackService.getCurrentWeather(capture(captures)) }
            Assert.assertEquals("metric", captures.first())
            Assert.assertNotNull(response.getOrNull())
        }

    @Test
    fun `GIVEN weather details repo WHEN get weather details is called with metric unit AND Call to open weather failed THEN call the weather stack api as a fallback`() =
        runTest {
            // setup
            val captures = mutableListOf<String>()
            coEvery { mocked.openWeatherMapService.getCurrentWeather(any()) } returns Response.error(
                500,
                ResponseBody.create(null, "")
            )

            // trigger
            repo.getWeatherDetails(UnitType.Metric)

            // verify
            coVerify(exactly = 1) { mocked.openWeatherMapService.getCurrentWeather(capture(captures)) }
            coVerify(exactly = 1) { mocked.weatherStackService.getCurrentWeather(capture(captures)) }
            Assert.assertEquals("metric", captures.first())
            Assert.assertEquals("m", captures[1])
        }
}