package com.moonwave.music.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.moonwave.music.R
import com.moonwave.music.core.model.Song
import com.moonwave.music.ui.theme.ElectricBlue
import com.moonwave.music.ui.theme.PitchBlack

@Composable
fun AnimatedVinyl(
    song: Song?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    rotationDurationMs: Int = 12000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "VinylRotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = rotationDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "VinylAngle"
    )

    val currentRotation = if (isPlaying) angle else 0f

    Box(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .aspectRatio(1f)
            .shadow(24.dp, shape = CircleShape)
            .rotate(currentRotation),
        contentAlignment = Alignment.Center
    ) {
        // Vinyl Outer Record Disc
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f

            // Disc Body
            drawCircle(
                color = Color(0xFF14171E),
                radius = radius,
                center = center
            )

            // Radial Sheen
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF232A3B), Color(0xFF0F1218)),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // Grooves
            val grooveCount = 8
            for (i in 1..grooveCount) {
                val grooveRadius = radius * (0.45f + (i.toFloat() / grooveCount) * 0.5f)
                drawCircle(
                    color = Color(0x22FFFFFF),
                    radius = grooveRadius,
                    center = center,
                    style = Stroke(width = 1.2f)
                )
            }

            // Outer rim border
            drawCircle(
                color = Color(0xFF2C3549),
                radius = radius - 1f,
                center = center,
                style = Stroke(width = 2.5f)
            )
        }

        // Center Album Art Label
        Box(
            modifier = Modifier
                .fillMaxSize(0.42f)
                .clip(CircleShape)
                .border(3.dp, ElectricBlue.copy(alpha = 0.6f), CircleShape)
                .background(PitchBlack),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song?.albumArtUri)
                    .crossfade(true)
                    .error(R.drawable.ic_moonwave_logo)
                    .placeholder(R.drawable.ic_moonwave_logo)
                    .build(),
                contentDescription = "Vinyl Label",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Center Spindle Hole
            Box(
                modifier = Modifier
                    .fillMaxSize(0.18f)
                    .clip(CircleShape)
                    .background(Color(0xFF0B0E14))
                    .border(1.5.dp, Color(0xFF94A3B8), CircleShape)
            )
        }
    }
}
