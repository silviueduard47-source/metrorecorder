package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.WeatherIconType
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MetroWeatherIcon(
    type: WeatherIconType,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = (w * 0.07f).coerceAtLeast(2f)

        when (type) {
            WeatherIconType.SUN -> {
                val cx = w * 0.5f
                val cy = h * 0.5f
                val r = w * 0.22f
                // Sun center circle
                drawCircle(color = tint, radius = r, center = Offset(cx, cy))
                // Sun rays (8 rays)
                val rayInner = w * 0.32f
                val rayOuter = w * 0.44f
                for (i in 0 until 8) {
                    val angle = Math.toRadians((i * 45).toDouble())
                    val start = Offset(
                        cx + (rayInner * cos(angle)).toFloat(),
                        cy + (rayInner * sin(angle)).toFloat()
                    )
                    val end = Offset(
                        cx + (rayOuter * cos(angle)).toFloat(),
                        cy + (rayOuter * sin(angle)).toFloat()
                    )
                    drawLine(
                        color = tint,
                        start = start,
                        end = end,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            WeatherIconType.MOON -> {
                val path = Path().apply {
                    val r = w * 0.38f
                    val cx = w * 0.5f
                    val cy = h * 0.5f
                    moveTo(cx, cy - r)
                    cubicTo(cx + r * 0.9f, cy - r * 0.6f, cx + r * 0.9f, cy + r * 0.6f, cx, cy + r)
                    cubicTo(cx + r * 0.2f, cy + r * 0.4f, cx + r * 0.2f, cy - r * 0.4f, cx, cy - r)
                    close()
                }
                drawPath(path = path, color = tint)
            }

            WeatherIconType.SUN_CLOUD -> {
                // Sun peek at top right
                val sunX = w * 0.68f
                val sunY = h * 0.32f
                drawCircle(color = tint, radius = w * 0.16f, center = Offset(sunX, sunY))
                // Cloud foreground
                drawMetroCloud(this, w, h, tint, offsetY = h * 0.15f, scale = 0.85f)
            }

            WeatherIconType.MOON_CLOUD -> {
                // Moon peek at top right
                val cx = w * 0.68f
                val cy = h * 0.32f
                val r = w * 0.2f
                val moonPath = Path().apply {
                    moveTo(cx, cy - r)
                    cubicTo(cx + r * 0.8f, cy - r * 0.5f, cx + r * 0.8f, cy + r * 0.5f, cx, cy + r)
                    cubicTo(cx + r * 0.1f, cy + r * 0.3f, cx + r * 0.1f, cy - r * 0.3f, cx, cy - r)
                    close()
                }
                drawPath(path = moonPath, color = tint)
                drawMetroCloud(this, w, h, tint, offsetY = h * 0.15f, scale = 0.85f)
            }

            WeatherIconType.CLOUD -> {
                drawMetroCloud(this, w, h, tint)
            }

            WeatherIconType.DRIZZLE -> {
                drawMetroCloud(this, w, h, tint, offsetY = -h * 0.08f, scale = 0.85f)
                // Small rain drops
                val dropLen = h * 0.12f
                val y1 = h * 0.72f
                listOf(w * 0.32f, w * 0.52f, w * 0.72f).forEach { x ->
                    drawLine(
                        color = tint,
                        start = Offset(x, y1),
                        end = Offset(x - w * 0.06f, y1 + dropLen),
                        strokeWidth = strokeWidth * 0.8f,
                        cap = StrokeCap.Round
                    )
                }
            }

            WeatherIconType.RAIN -> {
                drawMetroCloud(this, w, h, tint, offsetY = -h * 0.08f, scale = 0.85f)
                // Rain streaks
                val dropLen = h * 0.18f
                val y1 = h * 0.7f
                listOf(w * 0.28f, w * 0.48f, w * 0.68f).forEach { x ->
                    drawLine(
                        color = tint,
                        start = Offset(x, y1),
                        end = Offset(x - w * 0.08f, y1 + dropLen),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            WeatherIconType.HEAVY_RAIN -> {
                drawMetroCloud(this, w, h, tint, offsetY = -h * 0.1f, scale = 0.85f)
                // Double rain streaks
                val dropLen = h * 0.18f
                listOf(
                    Offset(w * 0.25f, h * 0.65f),
                    Offset(w * 0.45f, h * 0.65f),
                    Offset(w * 0.65f, h * 0.65f),
                    Offset(w * 0.35f, h * 0.78f),
                    Offset(w * 0.55f, h * 0.78f),
                    Offset(w * 0.75f, h * 0.78f)
                ).forEach { pt ->
                    drawLine(
                        color = tint,
                        start = pt,
                        end = Offset(pt.x - w * 0.07f, pt.y + dropLen),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            WeatherIconType.SNOW -> {
                drawMetroCloud(this, w, h, tint, offsetY = -h * 0.08f, scale = 0.85f)
                // Snowflake dots
                val dotR = w * 0.045f
                listOf(
                    Offset(w * 0.3f, h * 0.74f),
                    Offset(w * 0.5f, h * 0.74f),
                    Offset(w * 0.7f, h * 0.74f),
                    Offset(w * 0.4f, h * 0.88f),
                    Offset(w * 0.6f, h * 0.88f)
                ).forEach { pt ->
                    drawCircle(color = tint, radius = dotR, center = pt)
                }
            }

            WeatherIconType.THUNDERSTORM -> {
                drawMetroCloud(this, w, h, tint, offsetY = -h * 0.12f, scale = 0.85f)
                // Lightning bolt
                val bolt = Path().apply {
                    moveTo(w * 0.54f, h * 0.58f)
                    lineTo(w * 0.42f, h * 0.76f)
                    lineTo(w * 0.52f, h * 0.76f)
                    lineTo(w * 0.40f, h * 0.96f)
                    lineTo(w * 0.62f, h * 0.72f)
                    lineTo(w * 0.50f, h * 0.72f)
                    close()
                }
                drawPath(path = bolt, color = tint)
            }

            WeatherIconType.FOG -> {
                // Horizontal fog lines
                val yBase = h * 0.3f
                val lineH = h * 0.12f
                for (i in 0 until 4) {
                    val y = yBase + i * lineH
                    val xStart = if (i % 2 == 0) w * 0.15f else w * 0.25f
                    val xEnd = if (i % 2 == 0) w * 0.85f else w * 0.75f
                    drawLine(
                        color = tint,
                        start = Offset(xStart, y),
                        end = Offset(xEnd, y),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            WeatherIconType.WIND -> {
                val path1 = Path().apply {
                    moveTo(w * 0.15f, h * 0.4f)
                    lineTo(w * 0.65f, h * 0.4f)
                    cubicTo(w * 0.8f, h * 0.4f, w * 0.85f, h * 0.28f, w * 0.75f, h * 0.22f)
                    cubicTo(w * 0.65f, h * 0.18f, w * 0.58f, h * 0.25f, w * 0.62f, h * 0.32f)
                }
                drawPath(path = path1, color = tint, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

                val path2 = Path().apply {
                    moveTo(w * 0.2f, h * 0.6f)
                    lineTo(w * 0.75f, h * 0.6f)
                    cubicTo(w * 0.9f, h * 0.6f, w * 0.92f, h * 0.72f, w * 0.82f, h * 0.78f)
                    cubicTo(w * 0.72f, h * 0.82f, w * 0.66f, h * 0.75f, w * 0.7f, h * 0.68f)
                }
                drawPath(path = path2, color = tint, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
            }
        }
    }
}

private fun drawMetroCloud(
    scope: androidx.compose.ui.graphics.drawscope.DrawScope,
    w: Float,
    h: Float,
    color: Color,
    offsetY: Float = 0f,
    scale: Float = 1.0f
) {
    val cx = w * 0.5f
    val cy = h * 0.5f + offsetY

    // Clean geometric cloud geometry with bottom flat bar and round puffs
    val cloudWidth = w * 0.75f * scale
    val cloudHeight = h * 0.38f * scale
    val left = cx - cloudWidth / 2f
    val top = cy - cloudHeight / 2f

    // Bottom pill
    scope.drawRoundRect(
        color = color,
        topLeft = Offset(left, cy + cloudHeight * 0.05f),
        size = Size(cloudWidth, cloudHeight * 0.55f),
        cornerRadius = CornerRadius(cloudHeight * 0.27f, cloudHeight * 0.27f)
    )

    // Center higher puff
    scope.drawCircle(
        color = color,
        radius = cloudHeight * 0.5f,
        center = Offset(cx - cloudWidth * 0.06f, cy - cloudHeight * 0.05f)
    )

    // Right puff
    scope.drawCircle(
        color = color,
        radius = cloudHeight * 0.36f,
        center = Offset(cx + cloudWidth * 0.25f, cy + cloudHeight * 0.12f)
    )

    // Left puff
    scope.drawCircle(
        color = color,
        radius = cloudHeight * 0.32f,
        center = Offset(cx - cloudWidth * 0.28f, cy + cloudHeight * 0.16f)
    )
}
