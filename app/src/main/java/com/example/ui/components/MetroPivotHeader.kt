package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalMetroAccent

@Composable
fun MetroPivotHeader(
    titles: List<String>,
    currentPage: Int,
    subtitle: String = "METRO WEATHER",
    onTitleClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val accent = LocalMetroAccent.current

    // Auto scroll when page changes
    LaunchedEffect(currentPage) {
        val targetScroll = (currentPage * 120).coerceIn(0, scrollState.maxValue)
        scrollState.animateScrollTo(targetScroll)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .testTag("metro_pivot_header")
    ) {
        // Subtle app tracker title (e.g. "METRO WEATHER" in accent color or subtle gray)
        Text(
            text = subtitle.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = accent.color,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.5.sp,
            modifier = Modifier.testTag("pivot_subtitle")
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Horizontal row of oversized pivot headings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(28.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            titles.forEachIndexed { index, title ->
                val isSelected = index == currentPage
                val alpha by animateFloatAsState(
                    targetValue = if (isSelected) 1.0f else 0.38f,
                    label = "pivot_alpha_$index"
                )

                Text(
                    text = title.lowercase(),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = if (isSelected) FontWeight.Light else FontWeight.ExtraLight,
                    fontSize = if (isSelected) 38.sp else 32.sp,
                    modifier = Modifier
                        .alpha(alpha)
                        .testTag("pivot_tab_$index")
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onTitleClick(index)
                        }
                )
            }
        }
    }
}
