package io.mx51.androidinterview.domain

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mx51.androidinterview.data.WeatherDetailsRepository
import io.mx51.androidinterview.data.model.UnitType
import io.mx51.androidinterview.data.model.WeatherDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetWeatherDetailsUseCaseTest {

    class Mocks {

        @MockK
        lateinit var repo: WeatherDetailsRepository

        init {
            MockKAnnotations.init(this, relaxUnitFun = true)
        }
    }

    lateinit var mocked: Mocks
    lateinit var weatherUseCase: GetWeatherDetailsUseCase

    @Before
    fun setup() {
        mocked = Mocks()
        weatherUseCase = GetWeatherDetailsUseCase(mocked.repo)
    }

    @Test
    fun `Given usecase When user tries to get weather And request is success Then return weather details`() =
        runTest {
            // setup
            coEvery { mocked.repo.getWeatherDetails(any()) } returns Result.success(WeatherDetails())

            // trigger
            val response = weatherUseCase.invoke(UnitType.Metric)

            // verify
            Assert.assertTrue(response.isSuccess)
            Assert.assertNotNull(response.getOrNull())

        }

    @Test
    fun `Given usecase When user tries to get weather And request is failure Then weather details is not returned`() =
        runTest {
            // setup
            coEvery { mocked.repo.getWeatherDetails(any()) } returns Result.failure(Throwable("ERROR"))

            // trigger
            val response = weatherUseCase.invoke(UnitType.Metric)

            // verify
            Assert.assertFalse(response.isSuccess)
            Assert.assertNotNull(response.exceptionOrNull())
            Assert.assertNull(response.getOrNull())

        }
}