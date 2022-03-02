package io.mx51.androidinterview.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.mx51.androidinterview.data.model.UnitType
import io.mx51.androidinterview.data.model.WeatherDetails
import io.mx51.androidinterview.domain.GetWeatherDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getWeatherDetailsUseCase: GetWeatherDetailsUseCase
) : ViewModel() {

    object WeatherView {

        data class State(
            val weatherDetails: WeatherDetails? = WeatherDetails(unitType = UnitType.Metric),
            val isLoading: Boolean = false,
            val errorText: String? = null,
            val isWindInMs: Boolean = true
        )

        sealed class Action {
            object AppStart : Action()
            object RefreshClick : Action()
            data class ChangeTemperature(val unitType: UnitType) : Action()
            data class ChangeWind(val windInMs: Boolean) : Action()
        }

    }

    private var viewState: WeatherView.State = WeatherView.State()

    private val _state = MutableStateFlow(viewState)
    val uiState = _state.asStateFlow()

    fun onUiAction(action: WeatherView.Action) {
        when (action) {
            WeatherView.Action.AppStart,
            WeatherView.Action.RefreshClick -> getWeatherDetails()
            is WeatherView.Action.ChangeTemperature -> onTempChange(action.unitType)
            is WeatherView.Action.ChangeWind -> onWindChange(action.windInMs)
        }
    }

    private fun onWindChange(windInMs: Boolean) {
        // 1 m/s = 1.94384 knot
        val existingWeatherDetails = viewState.weatherDetails
        viewState =
            viewState.copy(
                weatherDetails = existingWeatherDetails?.copy(
                    windSpeed = when {
                        windInMs -> {
                            existingWeatherDetails.windSpeed / 1.94384
                        }
                        else -> {
                            existingWeatherDetails.windSpeed * 1.94384
                        }
                    }
                ),
                isWindInMs = windInMs
            )
        emitViewState()
    }

    private fun onTempChange(unitType: UnitType) {
        val existingWeatherDetails = viewState.weatherDetails
        viewState =
            viewState.copy(
                weatherDetails = existingWeatherDetails?.copy(
                    temperature = when (unitType) {
                        UnitType.Imperial -> {
                            (existingWeatherDetails.temperature * 1.8) + 32
                        }
                        else -> {
                            (existingWeatherDetails.temperature - 32) * 0.5556
                        }
                    },
                    unitType = unitType
                ),
            )
        emitViewState()
    }

    private fun getWeatherDetails() {
        viewState = viewState.copy(isLoading = true, errorText = null)
        emitViewState()

        viewState.weatherDetails?.unitType?.let {
            viewModelScope.launch {
                // uses the unitType from the view state to keep user's preference
                getWeatherDetailsUseCase(it).fold(
                    { weatherDetails ->

                        viewState = viewState.copy(
                            weatherDetails = weatherDetails,
                            isLoading = false,
                        )

                        emitViewState()
                    }, { error ->
                        viewState = viewState.copy(
                            errorText = error.message,
                            isLoading = false,
                        )
                        emitViewState()
                    })
            }
        }
    }

    private fun emitViewState() {
        _state.value = viewState
    }
}