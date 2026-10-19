package com.example.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CityLocation
import com.example.data.model.FullWeatherReport
import com.example.data.model.TemperatureUnit
import com.example.data.preferences.UserPreferences
import com.example.data.repository.WeatherRepository
import com.example.ui.theme.MetroAccent
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WeatherUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val currentReport: FullWeatherReport? = null,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<CityLocation> = emptyList(),
    val savedCities: List<CityLocation> = UserPreferences.DEFAULT_CITIES,
    val pinnedReports: Map<String, FullWeatherReport> = emptyMap(),
    val isAccentPickerOpen: Boolean = false,
    val isSearchOverlayOpen: Boolean = false
)

class WeatherViewModel(
    application: Application,
    private val repository: WeatherRepository = WeatherRepository(),
    private val preferences: UserPreferences = UserPreferences(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(WeatherUiState(isLoading = true))
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    val accentFlow: StateFlow<MetroAccent> = preferences.accentFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MetroAccent.COBALT)

    val isDarkFlow: StateFlow<Boolean> = preferences.isDarkFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val tempUnitFlow: StateFlow<TemperatureUnit> = preferences.tempUnitFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TemperatureUnit.CELSIUS)

    val pinnedCityIdsFlow: StateFlow<Set<String>> = preferences.pinnedCityIdsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), setOf("seattle_wa", "london_uk", "tokyo_jp"))

    private var activeCity: CityLocation = UserPreferences.DEFAULT_CITIES.first()

    init {
        loadInitialWeather()
    }

    private fun loadInitialWeather() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            loadWeatherForCity(activeCity)
            loadPinnedForecasts()
        }
    }

    fun selectCity(city: CityLocation) {
        activeCity = city
        viewModelScope.launch {
            preferences.setSelectedCityId(city.id)
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            loadWeatherForCity(city)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            loadWeatherForCity(activeCity)
            loadPinnedForecasts()
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    private suspend fun loadWeatherForCity(city: CityLocation) {
        val result = repository.fetchWeather(city)
        result.fold(
            onSuccess = { report ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentReport = report,
                    errorMessage = null
                )
            },
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Failed to load weather"
                )
            }
        )
    }

    private suspend fun loadPinnedForecasts() {
        val pinnedIds = pinnedCityIdsFlow.value
        val citiesToFetch = _uiState.value.savedCities.filter { pinnedIds.contains(it.id) }
        val map = mutableMapOf<String, FullWeatherReport>()

        for (city in citiesToFetch) {
            val res = repository.fetchWeather(city)
            res.getOrNull()?.let { map[city.id] = it }
        }
        _uiState.value = _uiState.value.copy(pinnedReports = map)
    }

    fun searchLocations(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.trim().length < 2) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearching = false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            val result = repository.searchLocations(query)
            result.fold(
                onSuccess = { list ->
                    _uiState.value = _uiState.value.copy(searchResults = list, isSearching = false)
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearching = false)
                }
            )
        }
    }

    fun addCity(city: CityLocation) {
        val current = _uiState.value.savedCities
        if (current.none { it.id == city.id }) {
            val updated = listOf(city) + current
            _uiState.value = _uiState.value.copy(savedCities = updated)
        }
        selectCity(city)
        closeSearch()
    }

    fun removeCity(cityId: String) {
        val current = _uiState.value.savedCities
        if (current.size > 1) {
            val updated = current.filterNot { it.id == cityId }
            _uiState.value = _uiState.value.copy(savedCities = updated)
            if (activeCity.id == cityId) {
                selectCity(updated.first())
            }
        }
    }

    fun togglePin(cityId: String) {
        viewModelScope.launch {
            preferences.togglePinCity(cityId)
            loadPinnedForecasts()
        }
    }

    fun setAccent(accent: MetroAccent) {
        viewModelScope.launch {
            preferences.setAccent(accent)
            _uiState.value = _uiState.value.copy(isAccentPickerOpen = false)
        }
    }

    fun toggleDarkTheme() {
        viewModelScope.launch {
            preferences.setIsDark(!isDarkFlow.value)
        }
    }

    fun toggleUnits() {
        viewModelScope.launch {
            val next = if (tempUnitFlow.value == TemperatureUnit.CELSIUS) {
                TemperatureUnit.FAHRENHEIT
            } else {
                TemperatureUnit.CELSIUS
            }
            preferences.setTempUnit(next)
        }
    }

    fun openSearch() {
        _uiState.value = _uiState.value.copy(isSearchOverlayOpen = true, searchQuery = "", searchResults = emptyList())
    }

    fun closeSearch() {
        _uiState.value = _uiState.value.copy(isSearchOverlayOpen = false, searchQuery = "", searchResults = emptyList())
    }

    fun openAccentPicker() {
        _uiState.value = _uiState.value.copy(isAccentPickerOpen = true)
    }

    fun closeAccentPicker() {
        _uiState.value = _uiState.value.copy(isAccentPickerOpen = false)
    }

    @SuppressLint("MissingPermission")
    fun applyGpsLocation(location: Location) {
        val gpsCity = CityLocation(
            id = "gps_current",
            name = "Current Location",
            region = "GPS",
            country = "Local",
            latitude = location.latitude,
            longitude = location.longitude,
            isGpsLocation = true
        )
        addCity(gpsCity)
    }
}
