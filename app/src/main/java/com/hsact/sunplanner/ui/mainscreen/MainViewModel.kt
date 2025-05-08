package com.hsact.sunplanner.ui.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.sunplanner.data.responses.Location
import com.hsact.sunplanner.data.repository.WeatherRepository
import com.hsact.sunplanner.domain.usecase.weather.AggregateWeatherByDateUseCase
import com.hsact.sunplanner.domain.usecase.weather.CreateWeatherGraphBarsUseCase
import com.hsact.sunplanner.domain.usecase.weather.CreateWeatherGraphLineUseCase
import com.hsact.sunplanner.domain.usecase.weather.FetchFilteredWeatherUseCase
import com.hsact.sunplanner.data.network.WeatherRequestParams
import com.hsact.sunplanner.data.responses.WeatherResponse
import com.hsact.sunplanner.data.utils.DateUtils
import com.hsact.sunplanner.data.utils.StringProvider
import com.hsact.sunplanner.domain.usecase.settings.GetSettingsUseCase
import com.hsact.sunplanner.domain.usecase.settings.UpdateLocationUseCase
import com.hsact.sunplanner.ui.settings.modes.nameToLanguageMode
import com.hsact.sunplanner.ui.settings.modes.toName
import com.hsact.sunplanner.ui.settings.modes.unitModes.toName
import com.hsact.sunplanner.ui.theme.avgTempLineColor
import com.hsact.sunplanner.ui.theme.maxTempLineColor
import com.hsact.sunplanner.ui.theme.minTempLineColor
import com.hsact.sunplanner.ui.theme.precipitationBarColor
import com.hsact.sunplanner.ui.theme.sunShineLineColor
import com.hsact.sunplanner.ui.theme.windGustsSpeedColor
import com.hsact.sunplanner.ui.theme.windSpeedColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject
import kotlin.math.round
import kotlin.math.roundToInt

@FlowPreview
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val stringProvider: StringProvider,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val fetchFilteredWeatherUseCase: FetchFilteredWeatherUseCase,
    private val aggregateWeatherByDateUseCase: AggregateWeatherByDateUseCase,
    private val createWeatherGraphLineUseCase: CreateWeatherGraphLineUseCase,
    private val createWeatherGraphBarsUseCase: CreateWeatherGraphBarsUseCase
) : ViewModel() {

    private val _mainUiState = MutableStateFlow(MainUIState())
    val mainUiState: StateFlow<MainUIState> get() = _mainUiState

    init {
        viewModelScope.launch {
            getSettingsUseCase.location.collect { location ->
                _mainUiState.value = _mainUiState.value.copy(location = location)
            }
        }
        viewModelScope.launch {
            combine(
                getSettingsUseCase.language,
                getSettingsUseCase.theme,
                getSettingsUseCase.temperatureUnit,
                getSettingsUseCase.windUnit,
                getSettingsUseCase.precipitationUnit
            ) { language, theme,tempUnit, windUnit, precipitationUnit ->
                _mainUiState.value.copy(
                    languageMode = language?: nameToLanguageMode(Locale.getDefault().language),
                    themeMode = theme,
                    temperatureUnitMode = tempUnit,
                    windUnitMode = windUnit,
                    precipitationUnitMode = precipitationUnit
                )
            }.debounce(200).collect { updatedUiState ->
                _mainUiState.value = updatedUiState
                if (_mainUiState.value.weatherData != null && !_mainUiState.value.isLoading) {
                    fetchWeather(prepareParamsForRequest(updatedUiState)?: return@collect)
                }
            }
        }
    }

    fun updateLocation(city: Location) {
        //_searchDataUI.value = _searchDataUI.value.copy(location = city)
        viewModelScope.launch {
            updateLocationUseCase.invoke(city)
        }
    }

    fun updateStartYear(year: Int) {
        val old = _mainUiState.value.startLD
        val newDate = old.withYear(year).coerceDay()
        _mainUiState.value = _mainUiState.value.copy(startLD = newDate)
    }

    fun updateStartMonth(month: Int) {
        val old = _mainUiState.value.startLD
        val newDate = old.withMonth(month).coerceDay()
        _mainUiState.value = _mainUiState.value.copy(startLD = newDate)
    }

    fun updateStartDay(day: Int) {
        val old = _mainUiState.value.startLD
        val maxDay = old.lengthOfMonth()
        val validDay = day.coerceIn(1, maxDay)
        val newDate = old.withDayOfMonth(validDay)
        _mainUiState.value = _mainUiState.value.copy(startLD = newDate)
    }

    fun updateEndYear(year: Int) {
        val old = _mainUiState.value.endLD
        val newDate = old.withYear(year).coerceDay()
        _mainUiState.value = _mainUiState.value.copy(endLD = newDate)
    }

    fun updateEndMonth(month: Int) {
        val old = _mainUiState.value.endLD
        val newDate = old.withMonth(month).coerceDay()
        _mainUiState.value = _mainUiState.value.copy(endLD = newDate)
    }

    fun updateEndDay(day: Int) {
        val old = _mainUiState.value.endLD
        val maxDay = old.lengthOfMonth()
        val validDay = day.coerceIn(1, maxDay)
        val newDate = old.withDayOfMonth(validDay)
        _mainUiState.value = _mainUiState.value.copy(endLD = newDate)
    }

    private fun LocalDate.coerceDay(): LocalDate {
        val maxDay = this.lengthOfMonth()
        return if (this.dayOfMonth > maxDay) this.withDayOfMonth(maxDay) else this
    }

    fun updateError(error: String) {
        _mainUiState.value = _mainUiState.value.copy(error = error)
    }

    fun cleanError() {
        _mainUiState.value = _mainUiState.value.copy(error = "")
    }

    fun updateConfirmedLD(start: LocalDate, end: LocalDate) {
        _mainUiState.value =
            _mainUiState.value.copy(confirmedStartLD = start, confirmedEndLD = end)
    }

    fun onWeatherSearchClick() {
        if (_mainUiState.value.location == null) {
            updateError(stringProvider.locationEmpty())
            return
        }
        if (_mainUiState.value.startLD.year > _mainUiState.value.endLD.year) {
            updateError(stringProvider.invalidYearRange())
            return
        }
        if (_mainUiState.value.startLD.withYear(_mainUiState.value.endLD.year).dayOfYear >
            _mainUiState.value.endLD.dayOfYear) {
            updateError(stringProvider.invalidDateRange())
            return
        }
        if (_mainUiState.value.endLD.year - _mainUiState.value.startLD.year >= 20) {
            //updateError("Years range is too big (max 20)")
            updateError(stringProvider.yearsRangeTooBig())
            return
        }
        val params = prepareParamsForRequest(_mainUiState.value)
        if (params != null) {
            updateConfirmedLD(_mainUiState.value.startLD, _mainUiState.value.endLD)
            fetchWeather(params)
        }
    }

    fun prepareParamsForRequest(state: MainUIState): WeatherRequestParams? {
        val location = state.location ?: return null
        val startDate = state.startLD
        val endDate = state.endLD
        val temperatureUnit = state.temperatureUnitMode.toName()
        val windSpeedUnit = state.windUnitMode.toName()
        val precipitationUnit = state.precipitationUnitMode.toName()

        return WeatherRequestParams().apply {
            latitude = location.latitude
            longitude = location.longitude
            this.startDate = startDate.toString() // YYYY-MM-DD
            this.endDate = endDate.toString()
            this.temperatureUnit = temperatureUnit
            this.windSpeedUnit = windSpeedUnit
            this.precipitationUnit = precipitationUnit
        }
    }

    fun fetchCityList(cityName: String) {
        var cities: List<Location>? = null
        viewModelScope.launch {
            try {
                cities = repository.getCitiesList(
                    cityName = cityName,
                    language = _mainUiState.value.languageMode.toName(),
                )
            } catch (e: Exception) {
                //updateError("Error fetching cities: ${e.message}")
                updateError(stringProvider.fetchCitiesError(e))
            }
            if (cities != null) {
                _mainUiState.value = _mainUiState.value.copy(cities = cities!!)
                println(cities)
            }
        }
    }

    private fun fetchWeather(params: WeatherRequestParams) {
        _mainUiState.value = _mainUiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val filteredWeather = fetchFilteredWeatherUseCase.execute(
                    params,
                    _mainUiState.value.startLD,
                    _mainUiState.value.endLD
                )
                saveWeatherData(filteredWeather)
            } catch (e: Exception) {
                updateError(stringProvider.fetchWeatherError(e))
            }
            _mainUiState.value = _mainUiState.value.copy(isLoading = false)
        }
    }

    fun saveWeatherData(data: WeatherResponse) {
        _mainUiState.value = _mainUiState.value.copy(weatherData = data)
        var maxTemps = _mainUiState.value.weatherData!!.daily.maxTemperature
        var minTemps = _mainUiState.value.weatherData!!.daily.minTemperature
        var averageTemps = maxTemps.indices.map { i ->
            val avg = (maxTemps[i] + minTemps[i]) / 2
            round(avg * 10) / 10
        }
        var sunshine = _mainUiState.value.weatherData!!.daily.sunshineDuration
            .map { ((it / 3600.0) * 10).roundToInt() / 10.0 }
        var precipitation = _mainUiState.value.weatherData!!.daily.precipitationSum
        var windSpeed = _mainUiState.value.weatherData!!.daily.windSpeedMax
        var gustSpeed = _mainUiState.value.weatherData!!.daily.windGustsMax

        if (_mainUiState.value.startLD.year == _mainUiState.value.endLD.year) {
            _mainUiState.value = _mainUiState.value.copy(isOneYear = true)
        } else {
            _mainUiState.value = _mainUiState.value.copy(isOneYear = false)
        }

        if (_mainUiState.value.startLD.dayOfMonth != _mainUiState.value.endLD.dayOfMonth ||
            _mainUiState.value.startLD.monthValue != _mainUiState.value.endLD.monthValue
        ) {
            _mainUiState.value = _mainUiState.value.copy(isOneDay = false)
            val aggregated = aggregateWeatherByDateUseCase.execute(data.daily)
            maxTemps = aggregated.map { it.avgMaxTemp }
            averageTemps = aggregated.map { it.avgAvgTemp }
            minTemps = aggregated.map { it.avgMinTemp }
            sunshine = aggregated.map { (it.avgSunshineSeconds / 3600.0 * 10).roundToInt() / 10.0 }
            precipitation = aggregated.map { it.avgPrecipitation }
            windSpeed = aggregated.map { it.avgWindSpeed }
            gustSpeed = aggregated.map { it.avgWindGustSpeed }
        } else {
            _mainUiState.value = _mainUiState.value.copy(isOneDay = true)
        }
        val popUpLabels = DateUtils.generatePopUpLabels(_mainUiState.value.startLD, _mainUiState.value.endLD,
            Locale.getDefault())
        _mainUiState.value.maxTemperature =
            createWeatherGraphLineUseCase.invoke(
                stringProvider.max(), maxTemps, popUpLabels,
                maxTempLineColor, _mainUiState.value.isOneYear
            )
        _mainUiState.value.avgTemperature =
            createWeatherGraphLineUseCase.invoke(
                stringProvider.avg(), averageTemps, popUpLabels,
                avgTempLineColor, _mainUiState.value.isOneYear
            )

        _mainUiState.value.minTemperature =
            createWeatherGraphLineUseCase.invoke(
                stringProvider.min(), minTemps, popUpLabels,
                minTempLineColor, _mainUiState.value.isOneYear
            )

        _mainUiState.value.sunDuration =
            createWeatherGraphLineUseCase.invoke(
                "", sunshine, popUpLabels,
                sunShineLineColor, _mainUiState.value.isOneYear
            )

        _mainUiState.value.precipitation =
            createWeatherGraphBarsUseCase.invoke("", precipitation, precipitationBarColor)

        _mainUiState.value.windSpeed =
            createWeatherGraphLineUseCase.invoke(
                stringProvider.wind(), windSpeed, popUpLabels,
                windSpeedColor, _mainUiState.value.isOneYear
            )

        _mainUiState.value.windGustsSpeed =
            createWeatherGraphLineUseCase.invoke(
                stringProvider.gusts(), gustSpeed, popUpLabels,
                windGustsSpeedColor, _mainUiState.value.isOneYear
            )
    }
}