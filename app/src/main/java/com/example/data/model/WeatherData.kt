package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    val results: List<GeocodingResult>? = null
)

@JsonClass(generateAdapter = true)
data class GeocodingResult(
    val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val country_code: String? = null,
    val admin1: String? = null,
    val timezone: String? = null
)

data class CityLocation(
    val id: String,
    val name: String,
    val region: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String = "auto",
    val isPinned: Boolean = false,
    val isGpsLocation: Boolean = false
) {
    val displaySubtitle: String
        get() = when {
            region.isNotBlank() && country.isNotBlank() -> "$region, $country"
            country.isNotBlank() -> country
            else -> ""
        }
}

@JsonClass(generateAdapter = true)
data class OpenMeteoForecastResponse(
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
    val timezone: String? = null,
    val current: CurrentUnitsResponse? = null,
    val hourly: HourlyUnitsResponse? = null,
    val daily: DailyUnitsResponse? = null
)

@JsonClass(generateAdapter = true)
data class CurrentUnitsResponse(
    val time: String? = null,
    val temperature_2m: Double? = 0.0,
    val relative_humidity_2m: Double? = 0.0,
    val apparent_temperature: Double? = 0.0,
    val is_day: Int? = 1,
    val precipitation: Double? = 0.0,
    val weather_code: Int? = 0,
    val cloud_cover: Double? = 0.0,
    val surface_pressure: Double? = 1013.0,
    val wind_speed_10m: Double? = 0.0,
    val wind_direction_10m: Double? = 0.0
)

@JsonClass(generateAdapter = true)
data class HourlyUnitsResponse(
    val time: List<String>? = null,
    val temperature_2m: List<Double>? = null,
    val relative_humidity_2m: List<Double>? = null,
    val precipitation_probability: List<Int>? = null,
    val weather_code: List<Int>? = null,
    val wind_speed_10m: List<Double>? = null,
    val uv_index: List<Double>? = null
)

@JsonClass(generateAdapter = true)
data class DailyUnitsResponse(
    val time: List<String>? = null,
    val weather_code: List<Int>? = null,
    val temperature_2m_max: List<Double>? = null,
    val temperature_2m_min: List<Double>? = null,
    val sunrise: List<String>? = null,
    val sunset: List<String>? = null,
    val precipitation_sum: List<Double>? = null,
    val precipitation_probability_max: List<Int>? = null,
    val uv_index_max: List<Double>? = null
)

// UI Domain Models
data class CurrentWeather(
    val tempC: Double,
    val feelsLikeC: Double,
    val humidity: Int,
    val weatherCode: Int,
    val windSpeedKmh: Double,
    val windDirectionDeg: Int,
    val surfacePressureHpa: Double,
    val cloudCoverPercent: Int,
    val precipitationMm: Double,
    val isDay: Boolean,
    val uvIndex: Double,
    val time: String
)

data class HourlyForecast(
    val timeFormatted: String, // e.g. "14:00"
    val isoTime: String,
    val tempC: Double,
    val weatherCode: Int,
    val precipProb: Int,
    val windSpeedKmh: Double,
    val uvIndex: Double
)

data class DailyForecast(
    val dateIso: String,
    val dayName: String, // e.g. "monday", "tuesday"
    val weatherCode: Int,
    val maxTempC: Double,
    val minTempC: Double,
    val precipProb: Int,
    val precipMm: Double,
    val uvIndexMax: Double,
    val sunriseTime: String,
    val sunsetTime: String
)

data class FullWeatherReport(
    val city: CityLocation,
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val fetchedAtMillis: Long = System.currentTimeMillis()
)

enum class TemperatureUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F");

    fun convert(tempC: Double): Int {
        return when (this) {
            CELSIUS -> Math.round(tempC).toInt()
            FAHRENHEIT -> Math.round(tempC * 9.0 / 5.0 + 32.0).toInt()
        }
    }
}

enum class WindUnit(val label: String) {
    KMH("km/h"),
    MPH("mph");

    fun convert(speedKmh: Double): String {
        return when (this) {
            KMH -> "${Math.round(speedKmh)} km/h"
            MPH -> "${Math.round(speedKmh * 0.621371)} mph"
        }
    }
}

object WeatherCodeHelper {
    fun getConditionName(code: Int, isDay: Boolean = true): String {
        return when (code) {
            0 -> if (isDay) "Clear Sunny" else "Clear Night"
            1 -> if (isDay) "Mainly Sunny" else "Mainly Clear"
            2 -> "Partly Cloudy"
            3 -> "Overcast"
            45 -> "Foggy"
            48 -> "Depositing Rime Fog"
            51 -> "Light Drizzle"
            53 -> "Moderate Drizzle"
            55 -> "Dense Drizzle"
            56, 57 -> "Freezing Drizzle"
            61 -> "Slight Rain"
            63 -> "Moderate Rain"
            65 -> "Heavy Rain"
            66, 67 -> "Freezing Rain"
            71 -> "Slight Snow"
            73 -> "Moderate Snow"
            75 -> "Heavy Snow Fall"
            77 -> "Snow Grains"
            80 -> "Slight Showers"
            81 -> "Moderate Showers"
            82 -> "Violent Showers"
            85, 86 -> "Snow Showers"
            95 -> "Thunderstorm"
            96, 99 -> "Severe Thunderstorm"
            else -> "Variably Cloudy"
        }
    }

    fun getShortCondition(code: Int): String {
        return when (code) {
            0 -> "Sunny"
            1, 2 -> "Partly Cloudy"
            3 -> "Overcast"
            45, 48 -> "Fog"
            51, 53, 55 -> "Drizzle"
            61, 63, 65, 80, 81, 82 -> "Rain"
            71, 73, 75, 77, 85, 86 -> "Snow"
            95, 96, 99 -> "Storm"
            else -> "Cloudy"
        }
    }

    fun getIconType(code: Int, isDay: Boolean = true): WeatherIconType {
        return when (code) {
            0 -> if (isDay) WeatherIconType.SUN else WeatherIconType.MOON
            1 -> if (isDay) WeatherIconType.SUN_CLOUD else WeatherIconType.MOON_CLOUD
            2 -> if (isDay) WeatherIconType.SUN_CLOUD else WeatherIconType.MOON_CLOUD
            3 -> WeatherIconType.CLOUD
            45, 48 -> WeatherIconType.FOG
            51, 53, 55 -> WeatherIconType.DRIZZLE
            61, 63 -> WeatherIconType.RAIN
            65, 80, 81, 82 -> WeatherIconType.HEAVY_RAIN
            66, 67, 71, 73, 75, 77, 85, 86 -> WeatherIconType.SNOW
            95, 96, 99 -> WeatherIconType.THUNDERSTORM
            else -> WeatherIconType.CLOUD
        }
    }
}

enum class WeatherIconType {
    SUN,
    MOON,
    SUN_CLOUD,
    MOON_CLOUD,
    CLOUD,
    FOG,
    DRIZZLE,
    RAIN,
    HEAVY_RAIN,
    SNOW,
    THUNDERSTORM,
    WIND
}
