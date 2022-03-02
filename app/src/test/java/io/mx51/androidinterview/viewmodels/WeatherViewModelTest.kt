package io.mx51.androidinterview.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mx51.androidinterview.data.WeatherDetailsRepository
import io.mx51.androidinterview.data.model.UnitType
import io.mx51.androidinterview.data.model.WeatherDetails
import io.mx51.androidinterview.domain.GetWeatherDetailsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    class Mocks {
        @MockK
        lateinit var weatherDetailsRepository: WeatherDetailsRepository

        init {
            MockKAnnotations.init(this, relaxUnitFun = true)
        }
    }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var viewModel: WeatherViewModel
    private lateinit var mocked: Mocks

    @Before
    fun setUp() {
        mocked = Mocks()
        viewModel = WeatherViewModel(
            getWeatherDetailsUseCase = GetWeatherDetailsUseCase(
                weatherDetailsRepository = mocked.weatherDetailsRepository
            )
        )
    }

    @Test
    fun `GIVEN view model is idle WHEN user performs refresh action THEN fetch the data`() =
        runTest {
            // setup
            coEvery { mocked.weatherDetailsRepository.getWeatherDetails(any()) } returns Result.success(
                WeatherDetails()
            )

            // trigger
            viewModel.onUiAction(WeatherViewModel.WeatherView.Action.RefreshClick)

            // verify
            coVerify(exactly = 1) { mocked.weatherDetailsRepository.getWeatherDetails(any()) }
        }

    @Test
    fun `GIVEN view model has last temperature data in celsius WHEN user performs action to convert temperature to Fahrenheit THEN emit the result`() =
        runTest {
            // setup
            coEvery { mocked.weatherDetailsRepository.getWeatherDetails(any()) } returns
                    Result.success(WeatherDetails(temperature = 20.85))
            viewModel.onUiAction(WeatherViewModel.WeatherView.Action.RefreshClick)

            // trigger
            viewModel.onUiAction(WeatherViewModel.WeatherView.Action.ChangeTemperature(unitType = UnitType.Imperial))

            // verify
            viewModel.uiState.test {
                assertEquals(69.53, awaitItem().weatherDetails?.temperature)
            }
        }

    @Test
    fun `GIVEN view model has last temperature data in Fahrenheit WHEN user performs action to convert temperature to Celsius THEN emit the result`() =
        runTest {
            // setup
            coEvery { mocked.weatherDetailsRepository.getWeatherDetails(any()) } returns
                    Result.success(WeatherDetails(temperature = 69.53))
            viewModel.onUiAction(WeatherViewModel.WeatherView.Action.RefreshClick)

            // trigger
            viewModel.onUiAction(WeatherViewModel.WeatherView.Action.ChangeTemperature(unitType = UnitType.Metric))

            // verify
            viewModel.uiState.test {
                assertEquals(
                    20.85.toString(),
                    String.format("%.2f", awaitItem().weatherDetails?.temperature)
                )
            }
        }

    @Test
    fun `GIVEN view model has last wind data in meters per sec WHEN user performs action to convert wind to knots THEN emit the result`() =
        runTest {
            // setup
            coEvery { mocked.weatherDetailsRepository.getWeatherDetails(any()) } returns
                    Result.success(WeatherDetails(windSpeed = 1.0))
            viewModel.onUiAction(WeatherViewModel.WeatherView.Action.RefreshClick)

            // trigger
            viewModel.onUiAction(WeatherViewModel.WeatherView.Action.ChangeWind(windInMs = false))

            // verify
            viewModel.uiState.test {
                assertEquals(
                    1.94.toString(),
                    String.format("%.2f", awaitItem().weatherDetails?.windSpeed)
                )
            }
        }

    @Test
    fun `GIVEN view model has last wind data in knots WHEN user performs action to convert wind to meters per sec THEN emit the result`() =
        runTest {
            // setup
            coEvery { mocked.weatherDetailsRepository.getWeatherDetails(any()) } returns
                    Result.success(WeatherDetails(windSpeed = 7.00))
            viewModel.onUiAction(WeatherViewModel.WeatherView.Action.RefreshClick)

            // trigger
            viewModel.onUiAction(WeatherViewModel.WeatherView.Action.ChangeWind(windInMs = true))

            // verify
            viewModel.uiState.test {
                assertEquals(
                    3.60.toString(),
                    String.format("%.1f", awaitItem().weatherDetails?.windSpeed)
                )
            }
        }
}