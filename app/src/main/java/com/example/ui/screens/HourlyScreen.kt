package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.HorizontalDivider
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
import com.example.data.model.HourlyForecast
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherCodeHelper
import com.example.ui.components.MetroWeatherIcon
import com.example.ui.theme.LocalMetroAccent

@Composable
fun HourlyScreen(
    report: FullWeatherReport?,
    tempUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    val accent = LocalMetroAccent.current

    if (report == null || report.hourly.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "no hourly data available",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return
    }

    val hourlyList = report.hourly

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .testTag("screen_hourly"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "24-hour timeline",
                style = MaterialTheme.typography.labelSmall,
                color = accent.color,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal hourly ribbon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                hourlyList.take(16).forEachIndexed { index, item ->
                    HourlyRibbonTile(
                        item = item,
                        isFirst = index == 0,
                        tempUnit = tempUnit,
                        accentColor = accent.color
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "detailed hours",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.5.sp
            )
        }

        itemsIndexed(hourlyList) { index, hourItem ->
            DetailedHourlyRow(
                item = hourItem,
                isNow = index == 0,
                tempUnit = tempUnit,
                accentColor = accent.color
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HourlyRibbonTile(
    item: HourlyForecast,
    isFirst: Boolean,
    tempUnit: TemperatureUnit,
    accentColor: Color
) {
    val isDark = MaterialTheme.colorScheme.background == Color.Black
    val bg = if (isFirst) accentColor else if (isDark) Color(0xFF1D1D1D) else Color(0xFFE5E5E5)
    val contentColor = if (isFirst) Color.White else MaterialTheme.colorScheme.onBackground
    val iconType = WeatherCodeHelper.getIconType(item.weatherCode)

    Box(
        modifier = Modifier
            .width(82.dp)
            .background(bg)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (isFirst) "now" else item.timeFormatted,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.85f),
                fontWeight = if (isFirst) FontWeight.Bold else FontWeight.Normal
            )
            MetroWeatherIcon(
                type = iconType,
                size = 28.dp,
                tint = if (isFirst) Color.White else accentColor
            )
            Text(
                text = "${tempUnit.convert(item.tempC)}°",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
            if (item.precipProb > 0) {
                Text(
                    text = "${item.precipProb}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isFirst) Color.White else accentColor,
                    fontSize = 10.sp
                )
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun DetailedHourlyRow(
    item: HourlyForecast,
    isNow: Boolean,
    tempUnit: TemperatureUnit,
    accentColor: Color
) {
    val conditionName = WeatherCodeHelper.getConditionName(item.weatherCode)
    val iconType = WeatherCodeHelper.getIconType(item.weatherCode)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time & condition
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (isNow) "now" else item.timeFormatted,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isNow) FontWeight.Bold else FontWeight.Normal,
                color = if (isNow) accentColor else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.width(52.dp)
            )
            MetroWeatherIcon(
                type = iconType,
                size = 24.dp,
                tint = accentColor
            )
            Text(
                text = conditionName.lowercase(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // Metrics: Rain %, Wind speed, Temp
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (item.precipProb > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "precipitation chance",
                        tint = accentColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${item.precipProb}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Air,
                    contentDescription = "wind",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${item.windSpeedKmh.toInt()} km/h",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Text(
                text = "${tempUnit.convert(item.tempC)}°",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.width(36.dp)
            )
        }
    }
}
