package io.mx51.androidinterview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.mx51.androidinterview.app_ui.LoadingScreen
import io.mx51.androidinterview.app_ui.WeatherDetailsScreen
import io.mx51.androidinterview.data.model.UnitType
import io.mx51.androidinterview.viewmodels.WeatherViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onUiAction(WeatherViewModel.WeatherView.Action.AppStart)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) {
                        setContent {
                            LoadingScreen()
                        }
                    } else {
                        state.weatherDetails?.let { weatherDetails ->
                            setContent {
                                WeatherDetailsScreen(
                                    locationName = weatherDetails.locationName,
                                    temperature = weatherDetails.temperature,
                                    windSpeed = weatherDetails.windSpeed,
                                    description = weatherDetails.description,
                                    onRefreshClicked = {
                                        viewModel.onUiAction(
                                            WeatherViewModel.WeatherView.Action.RefreshClick
                                        )
                                    },
                                    isUnitTypeMetric = weatherDetails.unitType == UnitType.Metric,
                                    isWindInMs = state.isWindInMs,
                                    onConvertTempClicked = { isMetric ->
                                        viewModel.onUiAction(
                                            WeatherViewModel.WeatherView.Action.ChangeTemperature(
                                                unitType = if (!isMetric) UnitType.Imperial else UnitType.Metric
                                            )
                                        )
                                    },
                                    onConvertWindClicked = { isWindInMs ->
                                        viewModel.onUiAction(
                                            WeatherViewModel.WeatherView.Action.ChangeWind(
                                                !isWindInMs
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}