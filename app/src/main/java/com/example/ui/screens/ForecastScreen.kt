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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyForecast
import com.example.data.model.FullWeatherReport
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherCodeHelper
import com.example.ui.components.MetroWeatherIcon
import com.example.ui.theme.LocalMetroAccent

@Composable
fun ForecastScreen(
    report: FullWeatherReport?,
    tempUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    val accent = LocalMetroAccent.current

    if (report == null || report.daily.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "no forecast available",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .testTag("screen_forecast"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "7-day outlook",
                style = MaterialTheme.typography.labelSmall,
                color = accent.color,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(report.daily) { dailyItem ->
            DailyForecastRow(
                item = dailyItem,
                tempUnit = tempUnit,
                accentColor = accent.color
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                modifier = Modifier.padding(top = 14.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DailyForecastRow(
    item: DailyForecast,
    tempUnit: TemperatureUnit,
    accentColor: Color
) {
    var isExpanded by remember { mutableStateOf(false) }
    val conditionName = WeatherCodeHelper.getConditionName(item.weatherCode)
    val iconType = WeatherCodeHelper.getIconType(item.weatherCode)

    val maxTempFormatted = "${tempUnit.convert(item.maxTempC)}°"
    val minTempFormatted = "${tempUnit.convert(item.minTempC)}°"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .padding(vertical = 4.dp)
            .testTag("forecast_row_${item.dayName}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Day name & condition
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.dayName.lowercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = conditionName.lowercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    if (item.precipProb > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "precipitation",
                                tint = accentColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${item.precipProb}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Weather icon
            MetroWeatherIcon(
                type = iconType,
                size = 36.dp,
                tint = accentColor,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // High / Low temperatures
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = maxTempFormatted,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = minTempFormatted,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }

        // Expanded details (sunrise, sunset, UV, precipitation sum)
        if (isExpanded) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "sunrise",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        text = item.sunriseTime,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Column {
                    Text(
                        text = "sunset",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        text = item.sunsetTime,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Column {
                    Text(
                        text = "max uv",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "${item.uvIndexMax}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Column {
                    Text(
                        text = "rain sum",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "${item.precipMm} mm",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
