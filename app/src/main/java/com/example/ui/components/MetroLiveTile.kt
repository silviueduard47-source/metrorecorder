package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalMetroAccent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class MetroTileSize {
    SMALL,   // ~76dp
    MEDIUM,  // ~160dp
    WIDE     // Full width x ~160dp
}

@Composable
fun MetroLiveTile(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    enableAutoFlip: Boolean = true,
    flipIntervalMillis: Long = 6500L,
    onClick: (() -> Unit)? = null,
    frontContent: @Composable () -> Unit,
    backContent: (@Composable () -> Unit)? = null
) {
    val accent = LocalMetroAccent.current
    val tileBg = backgroundColor ?: accent.color

    // 3D Tilt interaction
    val tiltX = remember { Animatable(0f) }
    val tiltY = remember { Animatable(0f) }
    val tileScale = remember { Animatable(1f) }

    // Live Flip animation
    var isFlipped by remember { mutableStateOf(false) }
    val flipAngle = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    if (enableAutoFlip && backContent != null) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(flipIntervalMillis)
                val target = if (isFlipped) 0f else 180f
                flipAngle.animateTo(
                    targetValue = target,
                    animationSpec = tween(durationMillis = 650, easing = LinearEasing)
                )
                isFlipped = !isFlipped
            }
        }
    }

    Box(
        modifier = modifier
            .testTag("metro_live_tile")
            .graphicsLayer {
                // Apply 3D Perspective and rotation
                cameraDistance = 12f * density
                rotationX = flipAngle.value + tiltX.value
                rotationY = tiltY.value
                scaleX = tileScale.value
                scaleY = tileScale.value
            }
            .background(tileBg)
            .clipToBounds()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        // Calculate touch tilt based on touch position relative to tile center
                        val normX = (offset.x / size.width - 0.5f) * 14f
                        val normY = (offset.y / size.height - 0.5f) * -14f

                        scope.launch {
                            tiltX.animateTo(normY, spring(stiffness = Spring.StiffnessHigh))
                        }
                        scope.launch {
                            tiltY.animateTo(normX, spring(stiffness = Spring.StiffnessHigh))
                        }
                        scope.launch {
                            tileScale.animateTo(0.96f, spring(stiffness = Spring.StiffnessHigh))
                        }

                        val released = tryAwaitRelease()
                        scope.launch {
                            tiltX.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
                        }
                        scope.launch {
                            tiltY.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
                        }
                        scope.launch {
                            tileScale.animateTo(1f, spring(stiffness = Spring.StiffnessMedium))
                        }

                        if (released && onClick != null) {
                            onClick()
                        }
                    }
                )
            }
    ) {
        // Show front or back based on flip angle
        val showingFront = flipAngle.value < 90f || flipAngle.value > 270f
        if (showingFront || backContent == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                frontContent()
            }
        } else {
            // Back face requires flipping text back so it is not mirrored
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationX = 180f
                    }
                    .padding(12.dp)
            ) {
                backContent()
            }
        }
    }
}
