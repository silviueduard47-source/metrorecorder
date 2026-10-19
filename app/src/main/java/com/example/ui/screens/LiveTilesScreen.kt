package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CityLocation
import com.example.data.model.FullWeatherReport
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherCodeHelper
import com.example.ui.components.MetroLiveTile
import com.example.ui.components.MetroWeatherIcon
import com.example.ui.theme.LocalMetroAccent
import com.example.ui.theme.MetroWhite

@Composable
fun LiveTilesScreen(
    currentReport: FullWeatherReport?,
    savedCities: List<CityLocation>,
    pinnedReports: Map<String, FullWeatherReport>,
    tempUnit: TemperatureUnit,
    onSelectCity: (CityLocation) -> Unit,
    onManageCities: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalMetroAccent.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .testTag("screen_live_tiles"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "windows phone live tiles",
                style = MaterialTheme.typography.labelSmall,
                color = accent.color,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "tap any tile to tilt or switch city",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Primary Wide Tile (Active City)
        if (currentReport != null) {
            item {
                val current = currentReport.current
                val city = currentReport.city
                val conditionName = WeatherCodeHelper.getConditionName(current.weatherCode, current.isDay)
                val iconType = WeatherCodeHelper.getIconType(current.weatherCode, current.isDay)
                val tempFormatted = "${tempUnit.convert(current.tempC)}°"

                MetroLiveTile(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    enableAutoFlip = true,
                    flipIntervalMillis = 5000L,
                    onClick = { /* already selected */ },
                    frontContent = {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = city.name.lowercase(),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MetroWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "active",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MetroWhite.copy(alpha = 0.7f)
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = tempFormatted,
                                        style = MaterialTheme.typography.displayMedium,
                                        color = MetroWhite,
                                        fontWeight = FontWeight.Light
                                    )
                                    Text(
                                        text = conditionName.lowercase(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MetroWhite.copy(alpha = 0.9f),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                                Text(
                                    text = "wind ${current.windSpeedKmh.toInt()} km/h • humidity ${current.humidity}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MetroWhite.copy(alpha = 0.75f)
                                )
                            }
                        }
                    },
                    backContent = {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${city.name.lowercase()} • 24h summary",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetroWhite.copy(alpha = 0.8f)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "feels like",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MetroWhite.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${tempUnit.convert(current.feelsLikeC)}°",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MetroWhite
                                    )
                                }
                                Column {
                                    Text(
                                        text = "pressure",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MetroWhite.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${current.surfacePressureHpa.toInt()}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MetroWhite
                                    )
                                }
                                Column {
                                    Text(
                                        text = "uv rating",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MetroWhite.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${current.uvIndex}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MetroWhite
                                    )
                                }
                            }
                            Text(
                                text = "tap to return to front",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetroWhite.copy(alpha = 0.6f)
                            )
                        }
                    }
                )
            }
        }

        // Pinned Cities 2-Column Square Tiles
        val otherCities = savedCities.filter { it.id != currentReport?.city?.id }
        val cityPairs = otherCities.chunked(2)

        items(cityPairs) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val city1 = pair.getOrNull(0)
                val city2 = pair.getOrNull(1)

                if (city1 != null) {
                    val report1 = pinnedReports[city1.id]
                    CityMediumTile(
                        city = city1,
                        report = report1,
                        tempUnit = tempUnit,
                        onClick = { onSelectCity(city1) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (city2 != null) {
                    val report2 = pinnedReports[city2.id]
                    CityMediumTile(
                        city = city2,
                        report = report2,
                        tempUnit = tempUnit,
                        onClick = { onSelectCity(city2) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CityMediumTile(
    city: CityLocation,
    report: FullWeatherReport?,
    tempUnit: TemperatureUnit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tempFormatted = report?.let { "${tempUnit.convert(it.current.tempC)}°" } ?: "--°"
    val code = report?.current?.weatherCode ?: 0
    val isDay = report?.current?.isDay ?: true
    val condition = WeatherCodeHelper.getShortCondition(code)
    val iconType = WeatherCodeHelper.getIconType(code, isDay)

    MetroLiveTile(
        modifier = modifier.height(130.dp),
        enableAutoFlip = true,
        flipIntervalMillis = 6500L,
        onClick = onClick,
        frontContent = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = city.name.lowercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MetroWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = tempFormatted,
                        style = MaterialTheme.typography.displaySmall,
                        color = MetroWhite,
                        fontWeight = FontWeight.Light
                    )
                    MetroWeatherIcon(
                        type = iconType,
                        size = 32.dp,
                        tint = MetroWhite
                    )
                }
                Text(
                    text = condition.lowercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MetroWhite.copy(alpha = 0.85f)
                )
            }
        },
        backContent = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = city.name.lowercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MetroWhite.copy(alpha = 0.8f)
                )
                Column {
                    Text(
                        text = "tap to load",
                        style = MaterialTheme.typography.bodySmall,
                        color = MetroWhite.copy(alpha = 0.7f)
                    )
                    Text(
                        text = tempFormatted,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MetroWhite
                    )
                }
                Text(
                    text = city.country.lowercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MetroWhite.copy(alpha = 0.6f)
                )
            }
        }
    )
}
