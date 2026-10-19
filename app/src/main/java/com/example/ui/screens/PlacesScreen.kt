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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CityLocation
import com.example.data.model.FullWeatherReport
import com.example.data.model.TemperatureUnit
import com.example.ui.theme.LocalMetroAccent

@Composable
fun PlacesScreen(
    currentCityId: String?,
    savedCities: List<CityLocation>,
    pinnedCityIds: Set<String>,
    searchQuery: String,
    isSearching: Boolean,
    searchResults: List<CityLocation>,
    tempUnit: TemperatureUnit,
    pinnedReports: Map<String, FullWeatherReport>,
    onQueryChange: (String) -> Unit,
    onSelectCity: (CityLocation) -> Unit,
    onAddCity: (CityLocation) -> Unit,
    onRemoveCity: (String) -> Unit,
    onTogglePin: (String) -> Unit,
    onRequestGps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalMetroAccent.current
    val isDark = MaterialTheme.colorScheme.background == Color.Black

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .testTag("screen_places"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "manage places",
                style = MaterialTheme.typography.labelSmall,
                color = accent.color,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metro search box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_places"),
                placeholder = {
                    Text(
                        text = "search city or location...",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "search",
                        tint = accent.color
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "clear search",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accent.color,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = if (isDark) Color(0xFF141414) else Color(0xFFF0F0F0),
                    unfocusedContainerColor = if (isDark) Color(0xFF141414) else Color(0xFFF0F0F0),
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // GPS Location Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accent.color.copy(alpha = 0.15f))
                    .clickable { onRequestGps() }
                    .padding(14.dp)
                    .testTag("btn_use_gps_location"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "detect GPS location",
                    tint = accent.color,
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = "use my current location",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "detect weather via device gps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Search Results
        if (isSearching) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = accent.color,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "searching world locations...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        } else if (searchResults.isNotEmpty()) {
            item {
                Text(
                    text = "search results (${searchResults.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = accent.color,
                    letterSpacing = 1.5.sp
                )
            }

            items(searchResults) { result ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDark) Color(0xFF1E1E1E) else Color(0xFFEAEAEA))
                        .clickable { onAddCity(result) }
                        .padding(14.dp)
                        .testTag("search_result_${result.name}"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = result.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = result.displaySubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "add city",
                        tint = accent.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Saved Cities
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "saved places (${savedCities.size})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.5.sp
            )
        }

        items(savedCities) { city ->
            val isCurrent = city.id == currentCityId
            val isPinned = pinnedCityIds.contains(city.id)
            val cachedReport = pinnedReports[city.id]
            val tempFormatted = cachedReport?.let { "${tempUnit.convert(it.current.tempC)}°" }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isCurrent) accent.color.copy(alpha = 0.18f)
                        else if (isDark) Color(0xFF161616)
                        else Color(0xFFF3F3F3)
                    )
                    .clickable { onSelectCity(city) }
                    .padding(14.dp)
                    .testTag("saved_city_${city.id}"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = city.name.lowercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Light,
                            color = if (isCurrent) accent.color else MaterialTheme.colorScheme.onBackground
                        )
                        if (isCurrent) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• selected",
                                style = MaterialTheme.typography.labelSmall,
                                color = accent.color
                            )
                        }
                    }
                    if (city.displaySubtitle.isNotBlank()) {
                        Text(
                            text = city.displaySubtitle.lowercase(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (tempFormatted != null) {
                        Text(
                            text = tempFormatted,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Toggle Pin icon
                    IconButton(
                        onClick = { onTogglePin(city.id) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isPinned) Icons.Default.PushPin else Icons.Default.Pin,
                            contentDescription = if (isPinned) "unpin from tiles" else "pin to tiles",
                            tint = if (isPinned) accent.color else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Remove icon (if more than 1)
                    if (savedCities.size > 1) {
                        IconButton(
                            onClick = { onRemoveCity(city.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "remove place",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
