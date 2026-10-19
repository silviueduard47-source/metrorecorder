package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FullWeatherReport
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherCodeHelper
import com.example.ui.components.MetroLiveTile
import com.example.ui.components.MetroWeatherIcon
import com.example.ui.theme.LocalMetroAccent
import com.example.ui.theme.MetroWhite

@Composable
fun WeatherNowScreen(
    report: FullWeatherReport?,
    isLoading: Boolean,
    tempUnit: TemperatureUnit,
    onCityClick: () -> Unit,
    onGoToForecast: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalMetroAccent.current

    if (isLoading && report == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(color = accent.color)
                Text(
                    text = "loading weather...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Light
                )
            }
        }
        return
    }

    if (report == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "no weather data available",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return
    }

    val current = report.current
    val city = report.city
    val todayDaily = report.daily.firstOrNull()
    val conditionName = WeatherCodeHelper.getConditionName(current.weatherCode, current.isDay)
    val iconType = WeatherCodeHelper.getIconType(current.weatherCode, current.isDay)

    val currentTempFormatted = "${tempUnit.convert(current.tempC)}°"
    val feelsLikeFormatted = "${tempUnit.convert(current.feelsLikeC)}°"
    val maxTempFormatted = todayDaily?.let { "${tempUnit.convert(it.maxTempC)}°" } ?: currentTempFormatted
    val minTempFormatted = todayDaily?.let { "${tempUnit.convert(it.minTempC)}°" } ?: currentTempFormatted

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .testTag("screen_weather_now")
    ) {
        // City location header (Windows Phone large uppercase tracking)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onCityClick() }
                .padding(vertical = 4.dp)
                .testTag("now_city_header")
        ) {
            Icon(
                imageVector = Icons.Default.PinDrop,
                contentDescription = "location",
                tint = accent.color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = city.name.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (city.displaySubtitle.isNotBlank()) {
                    Text(
                        text = city.displaySubtitle.lowercase(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Giant Windows Phone Temperature + Weather Icon Hero Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = currentTempFormatted,
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 92.sp, lineHeight = 92.sp),
                    fontWeight = FontWeight.ExtraLight,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.testTag("now_current_temp")
                )
                Text(
                    text = conditionName.lowercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Light,
                    color = accent.color,
                    modifier = Modifier.testTag("now_condition_name")
                )
            }

            MetroWeatherIcon(
                type = iconType,
                size = 100.dp,
                tint = accent.color,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Feels like & High/Low banner
        Text(
            text = "feels like $feelsLikeFormatted • high $maxTempFormatted / low $minTempFormatted",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.testTag("now_temp_range")
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Windows Phone Live Tile Preview
        Text(
            text = "live tile preview (tap to flip)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Interactive Live Tile Hero Card
        MetroLiveTile(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            enableAutoFlip = true,
            flipIntervalMillis = 6000L,
            onClick = onGoToForecast,
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
                        Text(
                            text = city.name.lowercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MetroWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = currentTempFormatted,
                                style = MaterialTheme.typography.displayMedium,
                                color = MetroWhite,
                                fontWeight = FontWeight.Light
                            )
                            Text(
                                text = conditionName.lowercase(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MetroWhite.copy(alpha = 0.9f),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        Text(
                            text = "metro weather • live",
                            style = MaterialTheme.typography.labelSmall,
                            color = MetroWhite.copy(alpha = 0.7f)
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
                        text = "conditions at a glance",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetroWhite.copy(alpha = 0.8f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "humidity",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetroWhite.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${current.humidity}%",
                                style = MaterialTheme.typography.titleLarge,
                                color = MetroWhite
                            )
                        }
                        Column {
                            Text(
                                text = "wind",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetroWhite.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${current.windSpeedKmh.toInt()} km/h",
                                style = MaterialTheme.typography.titleLarge,
                                color = MetroWhite
                            )
                        }
                        Column {
                            Text(
                                text = "uv index",
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
                        text = "tap for 7-day forecast ->",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetroWhite.copy(alpha = 0.7f)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Glance metrics row (Metro flat boxes)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetroStatBox(
                icon = Icons.Default.WaterDrop,
                label = "humidity",
                value = "${current.humidity}%",
                modifier = Modifier.weight(1f)
            )
            MetroStatBox(
                icon = Icons.Default.Air,
                label = "wind speed",
                value = "${current.windSpeedKmh.toInt()} km/h",
                modifier = Modifier.weight(1f)
            )
            MetroStatBox(
                icon = Icons.Default.WbSunny,
                label = "uv index",
                value = "${current.uvIndex}",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun MetroStatBox(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Color.Black

    Box(
        modifier = modifier
            .background(if (isDark) Color(0xFF191919) else Color(0xFFE8E8E8))
            .padding(12.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = LocalMetroAccent.current.color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = label.lowercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontSize = 10.sp
            )
        }
    }
}
