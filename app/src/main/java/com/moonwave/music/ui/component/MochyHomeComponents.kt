package com.moonwave.music.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.moonwave.music.models.OnlineSong
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Mochy Music style Gacha random roll card.
 * Features an animated tumbling 3D die, gradient mesh backdrop, and roll action.
 */
@Composable
fun MochyGachaCard(
    pool: List<OnlineSong>,
    onPlaySong: (OnlineSong) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    var isRolling by remember { mutableStateOf(false) }
    var lastRolledSong by remember { mutableStateOf<OnlineSong?>(null) }

    val gradientBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFE85973),
                Color(0xFF8E44AD),
                Color(0xFF2C3E50)
            ),
            start = Offset(0f, 0f),
            end = Offset(800f, 600f)
        )
    }

    val squircleShape = SmoothCornerShape(24.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(elevation = 12.dp, shape = squircleShape, spotColor = Color(0xFFE85973).copy(alpha = 0.4f))
            .clip(squircleShape)
            .background(brush = gradientBrush)
            .press3D(maxTilt = 3f, pressedScale = 0.985f)
            .clickable {
                if (!isRolling && pool.isNotEmpty()) {
                    coroutineScope.launch {
                        isRolling = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        delay(650)
                        val randomSong = pool.random()
                        lastRolledSong = randomSong
                        isRolling = false
                        onPlaySong(randomSong)
                    }
                }
            }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (lastRolledSong != null && !isRolling) "YOU GOT!" else "GACHA HERE!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (lastRolledSong != null && !isRolling) {
                        lastRolledSong?.title ?: "Random plays"
                    } else {
                        "Random plays"
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(14.dp))

                // "roll!!" pill button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.92f))
                        .clickable {
                            if (!isRolling && pool.isNotEmpty()) {
                                coroutineScope.launch {
                                    isRolling = true
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    delay(650)
                                    val randomSong = pool.random()
                                    lastRolledSong = randomSong
                                    isRolling = false
                                    onPlaySong(randomSong)
                                }
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GachaDie(spinning = isRolling)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRolling) "rolling…" else "roll!!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2C3E50)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Overlapping squircle cover stack preview
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 70.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                val previewSong = lastRolledSong ?: pool.firstOrNull()
                val secondPreview = pool.getOrNull(1)

                if (secondPreview?.thumbnailUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(secondPreview.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(52.dp)
                            .graphicsLayer {
                                rotationZ = -12f
                                alpha = 0.65f
                            }
                            .clip(SmoothCornerShape(14.dp))
                    )
                }

                if (previewSong?.thumbnailUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(previewSong.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(60.dp)
                            .graphicsLayer {
                                rotationZ = 6f
                            }
                            .clip(SmoothCornerShape(16.dp))
                            .shadow(8.dp, SmoothCornerShape(16.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(60.dp)
                            .clip(SmoothCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        GachaDie(spinning = isRolling)
                    }
                }
            }
        }
    }
}

/**
 * Tumbling 3D die: white rounded square with 5 dark pips that spins during roll.
 */
@Composable
fun GachaDie(spinning: Boolean) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(spinning) {
        if (spinning) {
            while (true) {
                rotation.animateTo(rotation.value + 360f, tween(520, easing = LinearEasing))
            }
        } else {
            val rest = (rotation.value / 90f).roundToInt() * 90f
            rotation.animateTo(rest, spring(dampingRatio = 0.45f, stiffness = 320f))
        }
    }

    Canvas(
        modifier = Modifier
            .size(19.dp)
            .graphicsLayer { rotationZ = rotation.value }
    ) {
        drawRoundRect(color = Color.White, cornerRadius = CornerRadius(4.dp.toPx()))
        val pip = 1.9.dp.toPx()
        val near = size.width * 0.28f
        val far = size.width * 0.72f
        val mid = size.width / 2f
        val pipColor = Color(0xFF2C3E50)

        listOf(
            Offset(near, near), Offset(far, near),
            Offset(mid, mid),
            Offset(near, far), Offset(far, far)
        ).forEach {
            drawCircle(color = pipColor, radius = pip, center = it)
        }
    }
}

/**
 * Mochy "Recently Played >" carousel with squircle cards and gradient "Liked songs" heart card.
 */
@Composable
fun MochyRecentlyPlayedRow(
    recentSongs: List<OnlineSong>,
    onSongClick: (OnlineSong) -> Unit,
    onLikedClick: () -> Unit,
    onHeaderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onHeaderClick() }
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recently Played",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "See All",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // First item: Gradient "Liked songs" card
            item {
                val likedGradient = remember {
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFE85973), Color(0xFF9B51E0))
                    )
                }
                Column(
                    modifier = Modifier
                        .width(108.dp)
                        .clickable { onLikedClick() },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .shadow(6.dp, SmoothCornerShape(20.dp))
                            .clip(SmoothCornerShape(20.dp))
                            .background(likedGradient)
                            .press3D(maxTilt = 4f, pressedScale = 0.95f),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Liked Songs",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Liked Songs",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Auto playlist",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            // Recent songs with Mochy SmoothCornerShape
            items(recentSongs) { song ->
                Column(
                    modifier = Modifier
                        .width(108.dp)
                        .clickable { onSongClick(song) },
                    horizontalAlignment = Alignment.Start
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(song.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = song.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(108.dp)
                            .shadow(4.dp, SmoothCornerShape(20.dp))
                            .clip(SmoothCornerShape(20.dp))
                            .press3D(maxTilt = 4f, pressedScale = 0.95f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = song.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
