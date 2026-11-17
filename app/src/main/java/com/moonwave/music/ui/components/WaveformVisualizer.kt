package com.moonwave.music.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moonwave.music.ui.theme.ElectricBlue
import com.moonwave.music.ui.theme.NeonCyan
import com.moonwave.music.ui.theme.SubtlePurple
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 16,
    barWidth: Dp = 3.dp,
    barSpacing: Dp = 2.dp,
    maxHeight: Dp = 24.dp,
    activeColor: Color = ElectricBlue
) {
    val heights = remember(barCount) {
        List(barCount) { Animatable(0.2f) }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                heights.forEachIndexed { index, animatable ->
                    val target = 0.15f + Random.nextFloat() * 0.85f
                    animatable.animateTo(
                        targetValue = target,
                        animationSpec = tween(
                            durationMillis = 150 + (index * 25) % 150,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
                delay(40)
            }
        } else {
            heights.forEach { animatable ->
                animatable.animateTo(
                    targetValue = 0.2f,
                    animationSpec = tween(300)
                )
            }
        }
    }

    val totalWidth = (barWidth + barSpacing) * barCount

    Canvas(
        modifier = modifier
            .width(totalWidth)
            .height(maxHeight)
    ) {
        val strokeWidthPx = barWidth.toPx()
        val spacingPx = barSpacing.toPx()
        val heightPx = size.height

        val gradient = Brush.verticalGradient(
            colors = listOf(ElectricBlue, NeonCyan, SubtlePurple)
        )

        heights.forEachIndexed { i, anim ->
            val barH = heightPx * anim.value
            val x = i * (strokeWidthPx + spacingPx)
            val y = heightPx - barH

            drawRoundRect(
                brush = gradient,
                topLeft = Offset(x, y),
                size = Size(strokeWidthPx, barH),
                cornerRadius = CornerRadius(strokeWidthPx / 2f, strokeWidthPx / 2f)
            )
        }
    }
}
