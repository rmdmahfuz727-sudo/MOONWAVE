package com.moonwave.music.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Entrance motion for home feed cards. The index is wrapped so sections far down the feed do not
 * wait for an ever-growing delay before appearing.
 */
@Composable
fun Modifier.enterStaggered(index: Int = 0, enabled: Boolean = true): Modifier {
    val delayMillis = (index.coerceAtLeast(0) % 8) * 65
    val progress by animateFloatAsState(
        targetValue = if (enabled) 1f else 0f,
        animationSpec = tween(
            durationMillis = 560,
            delayMillis = delayMillis,
            easing = FastOutSlowInEasing
        ),
        label = "HomeEnter$index"
    )

    return graphicsLayer {
        alpha = progress
        translationY = (1f - progress) * 26.dp.toPx()
        val scale = 0.94f + 0.06f * progress
        scaleX = scale
        scaleY = scale
        transformOrigin = TransformOrigin(0.5f, 1f)
    }
}

/**
 * Parallax and fade for the hero cards as the home list scrolls under the top area.
 * [scrollProvider] should read the LazyList scroll offset inside a snapshot-aware lambda.
 */
fun Modifier.homeScrollParallax(
    scrollProvider: () -> Float,
    fadeDistance: Float = 620f,
    translationFactor: Float = 0.08f,
    scaleAmount: Float = 0.025f
): Modifier = graphicsLayer {
    val scroll = scrollProvider().coerceAtLeast(0f)
    val progress = (scroll / fadeDistance).coerceIn(0f, 1f)
    alpha = (1f - progress).coerceIn(0f, 1f)
    translationY = -scroll * translationFactor
    val scale = 1f - progress * scaleAmount
    scaleX = scale
    scaleY = scale
}

/**
 * Slow, low-overhead aurora used behind the home feed. Three radial washes drift continuously and
 * are clipped to the available area; the parent controls overall visibility while scrolling.
 */
@Composable
fun AnimatedHomeAura(
    modifier: Modifier = Modifier,
    colors: List<Color> = emptyList()
) {
    val transition = rememberInfiniteTransition(label = "HomeAura")
    val phaseA by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse),
        label = "HomeAuraA"
    )
    val phaseB by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Reverse),
        label = "HomeAuraB"
    )

    val auraColors = remember(colors) {
        val fallback = listOf(
            Color(0xFFE85973),
            Color(0xFF8E44AD),
            Color(0xFF2C3E50)
        )
        val source = if (colors.size >= 3) colors.take(3) else fallback
        source.map { it.copy(alpha = 1f) }
    }

    Canvas(
        modifier = modifier.graphicsLayer { alpha = 0.55f }
    ) {
        val width = size.width
        val height = size.height
        if (width <= 0f || height <= 0f) return@Canvas

        fun wash(center: Offset, radius: Float, color: Color, strength: Float) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = strength), Color.Transparent),
                    center = center,
                    radius = radius
                ),
                center = center,
                radius = radius
            )
        }

        wash(
            center = Offset(width * (0.16f + 0.08f * phaseA), height * (0.10f + 0.06f * phaseB)),
            radius = width * 0.62f,
            color = auraColors[0],
            strength = 0.22f
        )
        wash(
            center = Offset(width * (0.82f - 0.06f * phaseB), height * (0.18f + 0.05f * phaseA)),
            radius = width * 0.58f,
            color = auraColors[1],
            strength = 0.18f
        )
        wash(
            center = Offset(width * (0.50f + 0.10f * (phaseA - 0.5f)), height * 0.02f),
            radius = width * 0.72f,
            color = auraColors[2],
            strength = 0.14f
        )
    }
}
