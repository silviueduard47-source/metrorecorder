package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.CityLocation
import com.example.data.model.TemperatureUnit
import com.example.ui.theme.MetroAccent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "metro_weather_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_ACCENT_INDEX = intPreferencesKey("accent_index")
        val KEY_IS_DARK = booleanPreferencesKey("is_dark")
        val KEY_TEMP_UNIT = stringPreferencesKey("temp_unit")
        val KEY_SELECTED_CITY_ID = stringPreferencesKey("selected_city_id")
        val KEY_PINNED_CITY_IDS = stringSetPreferencesKey("pinned_city_ids")
        val KEY_SAVED_CITIES_JSON = stringPreferencesKey("saved_cities_json")

        val DEFAULT_CITIES = listOf(
            CityLocation(
                id = "seattle_wa",
                name = "Seattle",
                region = "Washington",
                country = "United States",
                latitude = 47.6062,
                longitude = -122.3321,
                isPinned = true
            ),
            CityLocation(
                id = "london_uk",
                name = "London",
                region = "England",
                country = "United Kingdom",
                latitude = 51.5074,
                longitude = -0.1278,
                isPinned = true
            ),
            CityLocation(
                id = "tokyo_jp",
                name = "Tokyo",
                region = "Kanto",
                country = "Japan",
                latitude = 35.6762,
                longitude = 139.6503,
                isPinned = true
            ),
            CityLocation(
                id = "new_york_us",
                name = "New York",
                region = "New York",
                country = "United States",
                latitude = 40.7128,
                longitude = -74.0060,
                isPinned = false
            ),
            CityLocation(
                id = "paris_fr",
                name = "Paris",
                region = "Île-de-France",
                country = "France",
                latitude = 48.8566,
                longitude = 2.3522,
                isPinned = false
            ),
            CityLocation(
                id = "sydney_au",
                name = "Sydney",
                region = "New South Wales",
                country = "Australia",
                latitude = -33.8688,
                longitude = 151.2093,
                isPinned = false
            )
        )
    }

    val accentFlow: Flow<MetroAccent> = context.dataStore.data.map { prefs ->
        val index = prefs[KEY_ACCENT_INDEX] ?: 0
        MetroAccent.entries.getOrElse(index) { MetroAccent.COBALT }
    }

    val isDarkFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_DARK] ?: true
    }

    val tempUnitFlow: Flow<TemperatureUnit> = context.dataStore.data.map { prefs ->
        val name = prefs[KEY_TEMP_UNIT] ?: TemperatureUnit.CELSIUS.name
        try {
            TemperatureUnit.valueOf(name)
        } catch (_: Exception) {
            TemperatureUnit.CELSIUS
        }
    }

    val selectedCityIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_SELECTED_CITY_ID] ?: "seattle_wa"
    }

    val pinnedCityIdsFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_PINNED_CITY_IDS] ?: setOf("seattle_wa", "london_uk", "tokyo_jp")
    }

    suspend fun setAccent(accent: MetroAccent) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCENT_INDEX] = accent.ordinal
        }
    }

    suspend fun setIsDark(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_DARK] = isDark
        }
    }

    suspend fun setTempUnit(unit: TemperatureUnit) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TEMP_UNIT] = unit.name
        }
    }

    suspend fun setSelectedCityId(cityId: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SELECTED_CITY_ID] = cityId
        }
    }

    suspend fun togglePinCity(cityId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_PINNED_CITY_IDS] ?: setOf("seattle_wa", "london_uk", "tokyo_jp")
            prefs[KEY_PINNED_CITY_IDS] = if (current.contains(cityId)) {
                current - cityId
            } else {
                current + cityId
            }
        }
    }
}
