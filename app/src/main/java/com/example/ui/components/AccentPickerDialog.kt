package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MetroAccent
import com.example.ui.theme.MetroWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccentPickerDialog(
    currentAccent: MetroAccent,
    isDark: Boolean,
    onAccentSelected: (MetroAccent) -> Unit,
    onToggleDark: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_accent_picker")
    ) {
        Surface(
            color = if (isDark) Color(0xFF181818) else Color(0xFFEEEEEE),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "choose accent color",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "close",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dark / Light switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleDark() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "oled dark mode",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isDark) "deep black background" else "light background",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                    Switch(
                        checked = isDark,
                        onCheckedChange = { onToggleDark() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MetroWhite,
                            checkedTrackColor = currentAccent.color
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "windows phone accents",
                    style = MaterialTheme.typography.labelSmall,
                    color = currentAccent.color,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4-column Grid of Metro Accents
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(MetroAccent.entries) { accentItem ->
                        val isSelected = accentItem == currentAccent
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(accentItem.color)
                                .then(
                                    if (isSelected) Modifier.border(3.dp, MetroWhite)
                                    else Modifier
                                )
                                .clickable {
                                    onAccentSelected(accentItem)
                                }
                                .testTag("accent_${accentItem.displayName}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "selected",
                                    tint = MetroWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "current: ${currentAccent.displayName.lowercase()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
