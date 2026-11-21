package com.moonwave.music.ui.player

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sin

/**
 * Moonwave Music style musical interlude countdown dots.
 * Displays 3 rhythmic pulsing glowing dots during instrumental breaks between lyric lines.
 */
@Composable
fun LyricsInterludeDots(
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF38BDF8),
    dotSize: Dp = 10.dp,
    dotSpacing: Dp = 16.dp
) {
    val phase = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        phase.animateTo(
            targetValue = (2 * Math.PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(width = dotSpacing * 4, height = dotSize * 2)) {
            val p = phase.value
            val dotRadius = dotSize.toPx() / 2f
            val spacingPx = dotSpacing.toPx()
            val centerX = size.width / 2f
            val centerY = size.height / 2f

            for (i in -1..1) {
                val offset = (i + 1) * 0.8f
                val wave = ((sin(p + offset) + 1f) / 2f).coerceIn(0.25f, 1f)
                val cx = centerX + i * spacingPx
                val cy = centerY - (wave - 0.5f) * 6f

                // Ambient glow
                drawCircle(
                    color = activeColor.copy(alpha = wave * 0.35f),
                    radius = dotRadius * (1.2f + wave * 0.8f),
                    center = Offset(cx, cy)
                )

                // Core dot
                drawCircle(
                    color = activeColor.copy(alpha = wave),
                    radius = dotRadius * (0.8f + wave * 0.4f),
                    center = Offset(cx, cy)
                )
            }
        }
    }
}
