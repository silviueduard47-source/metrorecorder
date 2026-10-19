package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FullWeatherReport
import com.example.data.model.TemperatureUnit
import com.example.ui.theme.LocalMetroAccent

@Composable
fun DetailsScreen(
    report: FullWeatherReport?,
    tempUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    val accent = LocalMetroAccent.current

    if (report == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "no telemetry available",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return
    }

    val current = report.current
    val today = report.daily.firstOrNull()

    val windDirectionCompass = getCompassDirection(current.windDirectionDeg)
    val uvRisk = getUvRiskLevel(current.uvIndex)
    val humidityComfort = when {
        current.humidity < 30 -> "dry air"
        current.humidity in 30..60 -> "optimal comfort"
        current.humidity in 61..75 -> "humid"
        else -> "very humid"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .testTag("screen_details")
    ) {
        Text(
            text = "atmospheric telemetry",
            style = MaterialTheme.typography.labelSmall,
            color = accent.color,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2-column Metro grid of details
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetroTelemetryCard(
                icon = Icons.Default.WaterDrop,
                title = "humidity",
                value = "${current.humidity}%",
                subtitle = humidityComfort,
                modifier = Modifier.weight(1f)
            )
            MetroTelemetryCard(
                icon = Icons.Default.Air,
                title = "wind",
                value = "${current.windSpeedKmh.toInt()} km/h",
                subtitle = "$windDirectionCompass (${current.windDirectionDeg}°)",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetroTelemetryCard(
                icon = Icons.Default.WbSunny,
                title = "uv index",
                value = "${current.uvIndex}",
                subtitle = uvRisk,
                modifier = Modifier.weight(1f)
            )
            MetroTelemetryCard(
                icon = Icons.Default.Compress,
                title = "pressure",
                value = "${current.surfacePressureHpa.toInt()} hPa",
                subtitle = if (current.surfacePressureHpa > 1013) "high pressure" else "low pressure",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetroTelemetryCard(
                icon = Icons.Default.Cloud,
                title = "cloud cover",
                value = "${current.cloudCoverPercent}%",
                subtitle = when {
                    current.cloudCoverPercent < 20 -> "clear skies"
                    current.cloudCoverPercent in 20..60 -> "scattered clouds"
                    else -> "overcast"
                },
                modifier = Modifier.weight(1f)
            )
            MetroTelemetryCard(
                icon = Icons.Default.Thermostat,
                title = "feels like",
                value = "${tempUnit.convert(current.feelsLikeC)}°",
                subtitle = "actual: ${tempUnit.convert(current.tempC)}°",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sun & Daylight wide card
        if (today != null) {
            val isDark = MaterialTheme.colorScheme.background == Color.Black
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) Color(0xFF191919) else Color(0xFFE8E8E8))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbTwilight,
                            contentDescription = "sun schedule",
                            tint = accent.color,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "sun schedule",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "sunrise",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                            Text(
                                text = today.sunriseTime,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "sunset",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                            Text(
                                text = today.sunsetTime,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun MetroTelemetryCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Color.Black
    val accent = LocalMetroAccent.current

    Box(
        modifier = modifier
            .background(if (isDark) Color(0xFF191919) else Color(0xFFE8E8E8))
            .padding(14.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accent.color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = title.lowercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle.lowercase(),
                style = MaterialTheme.typography.bodySmall,
                color = accent.color,
                fontSize = 11.sp
            )
        }
    }
}

private fun getCompassDirection(degrees: Int): String {
    val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
    val index = Math.round((degrees % 360) / 22.5).toInt() % 16
    return directions[index]
}

private fun getUvRiskLevel(uv: Double): String {
    return when {
        uv < 3.0 -> "low risk"
        uv < 6.0 -> "moderate risk"
        uv < 8.0 -> "high risk"
        uv < 11.0 -> "very high risk"
        else -> "extreme risk"
    }
}
