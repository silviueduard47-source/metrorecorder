package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TemperatureUnit
import com.example.ui.theme.MetroBlack
import com.example.ui.theme.MetroDarkGray
import com.example.ui.theme.MetroWhite

@Composable
fun MetroAppBar(
    onRefresh: () -> Unit,
    onSearchClick: () -> Unit,
    onPlacesClick: () -> Unit,
    onAccentPickerClick: () -> Unit,
    onToggleUnits: () -> Unit,
    tempUnit: TemperatureUnit,
    isDark: Boolean,
    onToggleDark: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(if (isDark) MetroDarkGray else Color(0xFF202020))
            .navigationBarsPadding()
            .testTag("metro_app_bar")
    ) {
        // Expandable menu drawer
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                MetroMenuItem(
                    label = "switch unit (${if (tempUnit == TemperatureUnit.CELSIUS) "°C -> °F" else "°F -> °C"})",
                    onClick = {
                        onToggleUnits()
                        isExpanded = false
                    }
                )
                MetroMenuItem(
                    label = "switch theme (${if (isDark) "dark -> light" else "light -> dark"})",
                    onClick = {
                        onToggleDark()
                        isExpanded = false
                    }
                )
                MetroMenuItem(
                    label = "change accent color",
                    onClick = {
                        onAccentPickerClick()
                        isExpanded = false
                    }
                )
                MetroMenuItem(
                    label = "manage places & tiles",
                    onClick = {
                        onPlacesClick()
                        isExpanded = false
                    }
                )
                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                Text(
                    text = "metro weather • inspired by windows phone",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }

        // Standard 4-button application bar row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetroCircleButton(
                    icon = Icons.Default.Refresh,
                    label = "refresh",
                    onClick = onRefresh,
                    tag = "btn_appbar_refresh"
                )
                MetroCircleButton(
                    icon = Icons.Default.Search,
                    label = "search",
                    onClick = onSearchClick,
                    tag = "btn_appbar_search"
                )
                MetroCircleButton(
                    icon = Icons.Default.PinDrop,
                    label = "places",
                    onClick = onPlacesClick,
                    tag = "btn_appbar_places"
                )
                MetroCircleButton(
                    icon = Icons.Default.Palette,
                    label = "accent",
                    onClick = onAccentPickerClick,
                    tag = "btn_appbar_accent"
                )
            }

            // Three dots expander
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isExpanded = !isExpanded
                    }
                    .padding(8.dp)
                    .testTag("btn_appbar_more"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "more options",
                    tint = MetroWhite,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun MetroCircleButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .border(1.5.dp, MetroWhite, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MetroWhite,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MetroWhite.copy(alpha = 0.85f),
            fontSize = 9.sp
        )
    }
}

@Composable
private fun MetroMenuItem(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = label.lowercase(),
            style = MaterialTheme.typography.titleMedium,
            color = MetroWhite,
            fontWeight = FontWeight.Light
        )
    }
}
