package com.moonwave.music.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moonwave.music.R
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val LikeRed = Color(0xFFFF2D55)
private val LikePink = Color(0xFFFF8FAB)
private val PopEasing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)

private data class Particle(
    val angle: Float,
    val distance: Float,
    val size: Float,
    val color: Color
)

/**
 * Moonwave Music style heart like button with 3D tactile press and particle burst animation.
 */
@Composable
fun MoonwaveLikeButton(
    isLiked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    var burstTrigger by remember { mutableStateOf(0) }

    val particles = remember { mutableStateListOf<Particle>() }
    val burstProgress = remember { Animatable(0f) }

    LaunchedEffect(burstTrigger) {
        if (burstTrigger > 0) {
            particles.clear()
            val rng = Random(burstTrigger)
            val count = 8
            for (i in 0 until count) {
                val angle = (2f * PI * i / count).toFloat() + rng.nextFloat() * 0.3f
                particles.add(
                    Particle(
                        angle = angle,
                        distance = 45f + rng.nextFloat() * 25f,
                        size = 5f + rng.nextFloat() * 4f,
                        color = if (rng.nextBoolean()) LikeRed else LikePink
                    )
                )
            }
            burstProgress.snapTo(0f)
            burstProgress.animateTo(1f, tween(550, easing = PopEasing))
        }
    }

    Box(
        modifier = modifier.press3D(maxTilt = 6f, pressedScale = 0.92f),
        contentAlignment = Alignment.Center
    ) {
        // Particle canvas
        if (burstProgress.value in 0.01f..0.99f) {
            Canvas(modifier = Modifier.size(iconSize * 3)) {
                val progress = burstProgress.value
                val alpha = (1f - progress).coerceIn(0f, 1f)
                val centerOffset = Offset(size.width / 2f, size.height / 2f)

                particles.forEach { p ->
                    val dist = p.distance * progress * density
                    val px = centerOffset.x + cos(p.angle) * dist
                    val py = centerOffset.y + sin(p.angle) * dist
                    drawCircle(
                        color = p.color.copy(alpha = alpha),
                        radius = (p.size * (1f - progress * 0.5f)) * density / 2f,
                        center = Offset(px, py)
                    )
                }
            }
        }

        IconButton(
            onClick = {
                val willBeLiked = !isLiked
                if (willBeLiked) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    burstTrigger++
                    scope.launch {
                        scale.snapTo(0.7f)
                        scale.animateTo(1.35f, tween(180, easing = PopEasing))
                        scale.animateTo(1.0f, tween(200))
                    }
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onToggle()
            },
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
        ) {
            Icon(
                painter = painterResource(
                    id = if (isLiked) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                ),
                contentDescription = if (isLiked) "Unlike" else "Like",
                tint = if (isLiked) LikeRed else Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(iconSize)
            )
        }
    }
}
