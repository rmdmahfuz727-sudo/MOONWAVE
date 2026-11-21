package com.moonwave.music.ui.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moonwave.music.R
import com.moonwave.music.data.lyrics.LyricsResult
import com.moonwave.music.model.SongItem
import com.moonwave.music.ui.component.AppearancePrefs
import com.moonwave.music.ui.component.press3D
import com.moonwave.music.ui.theme.observeKey

/**
 * Custom Compose extension to render a marquee with smooth horizontal alpha-faded edges.
 * Uses drawWithCache to avoid allocating Brush and List objects on every frame of the drawing phase.
 */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.basicMarqueeWithFadedEdges(
    edgeWidth: Dp = 8.dp
): Modifier = this
    .basicMarquee()
    .padding(horizontal = edgeWidth)

internal fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

internal fun deriveArtworkSurfaceColor(
    sourceColor: Color,
    isDark: Boolean,
    darkLightness: Float,
    lightLightness: Float,
    darkSaturationRange: ClosedFloatingPointRange<Float>,
    lightSaturationRange: ClosedFloatingPointRange<Float>,
    monochromeSaturationThreshold: Float = 0.06f
): Color {
    val hsl = FloatArray(3)
    androidx.core.graphics.ColorUtils.colorToHSL(sourceColor.toArgb(), hsl)
    val isMonochrome = hsl[1] < monochromeSaturationThreshold
    hsl[2] = if (isDark) darkLightness else lightLightness
    hsl[1] = if (isMonochrome) {
        0f
    } else if (isDark) {
        hsl[1].coerceIn(darkSaturationRange.start, darkSaturationRange.endInclusive)
    } else {
        hsl[1].coerceIn(lightSaturationRange.start, lightSaturationRange.endInclusive)
    }
    return Color(androidx.core.graphics.ColorUtils.HSLToColor(hsl))
}

@Composable
internal fun SplitLikeDislikeChip(
    isLiked: Boolean,
    isDisliked: Boolean,
    likesCount: String,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    contentColor: Color,
    accentColor: Color,
    isDark: Boolean
) {
    val backgroundColor = contentColor.copy(alpha = if (isDark) 0.08f else 0.06f)
    val displayLikesText = remember(likesCount) {
        likesCount.ifBlank { "Like" }
    }
    
    Row(
        modifier = Modifier
            .height(36.dp)
            .press3D(maxTilt = 4f, pressedScale = 0.96f)
            .clip(CircleShape)
            .background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Like Button
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
                .clickable(onClick = onLikeClick)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(id = if (isLiked) R.drawable.ic_thumbup else R.drawable.ic_outline_thumbup),
                contentDescription = "Like",
                tint = if (isLiked) accentColor else contentColor.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = displayLikesText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = if (isLiked) accentColor else contentColor.copy(alpha = 0.8f)
            )
        }
        
        // Vertical Divider
        Spacer(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight(0.5f)
                .background(contentColor.copy(alpha = 0.15f))
        )
        
        // Dislike Button
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(topEnd = 18.dp, bottomEnd = 18.dp))
                .clickable(onClick = onDislikeClick)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = if (isDisliked) R.drawable.ic_thumbdown else R.drawable.ic_outline_thumbdown),
                contentDescription = "Dislike",
                tint = if (isDisliked) accentColor else contentColor.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
internal fun PillChip(
    icon: Any,
    label: String,
    onClick: () -> Unit,
    contentColor: Color,
    accentColor: Color,
    isDark: Boolean,
    isSelected: Boolean = false,
    progress: Float? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        accentColor.copy(alpha = if (isDark) 0.25f else 0.18f)
    } else {
        contentColor.copy(alpha = if (isDark) 0.08f else 0.06f)
    }
    
    val tintColor = if (isSelected) accentColor else contentColor.copy(alpha = 0.8f)
    val progressColor = accentColor.copy(alpha = if (isDark) 0.35f else 0.25f)
    
    val animatedFill by animateFloatAsState(
        targetValue = progress ?: 0f,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 150f),
        label = "DownloadChipProgress"
    )
    
    Box(
        modifier = modifier
            .height(36.dp)
            .press3D(maxTilt = 4f, pressedScale = 0.95f)
            .clip(CircleShape)
            .background(backgroundColor)
            .drawBehind {
                if (progress != null && progress > 0f) {
                    drawRect(
                        color = progressColor,
                        size = size.copy(width = size.width * animatedFill)
                    )
                }
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            when (icon) {
                is ImageVector -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = tintColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                is Painter -> {
                    Icon(
                        painter = icon,
                        contentDescription = label,
                        tint = tintColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = tintColor
            )
        }
    }
}

@Composable
internal fun WavySliderWithLabels(
    currentPositionProvider: () -> Int,
    duration: Int,
    isPlaying: Boolean,
    onSeekTo: (Int) -> Unit,
    dynamicAccentColor: Color,
    contentColor: Color,
    contentSecondary: Color,
    isDark: Boolean,
    audioCodec: String = "AAC",
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences(AppearancePrefs.PREFS, android.content.Context.MODE_PRIVATE)
    }
    val style by prefs.observeKey(AppearancePrefs.SEEK_BAR_STYLE, AppearancePrefs.SEEK_EXPRESSIVE)
    val showAudioCodec by prefs.observeKey(AppearancePrefs.SHOW_AUDIO_CODEC, true)

    var isScrubbing by remember { mutableStateOf(false) }
    var sliderScrubPos by remember { mutableFloatStateOf(0f) }

    val currentPosition = currentPositionProvider()
    val safeDur = if (duration > 0) duration.toFloat() else 1f
    val displayPos = if (isScrubbing) (sliderScrubPos * safeDur).toInt() else currentPosition
    val progressRatio = (currentPosition.toFloat() / safeDur).coerceIn(0f, 1f)
    val inactiveColor = contentColor.copy(alpha = if (isDark) 0.12f else 0.10f)

    val onScrub: (Float) -> Unit = {
        isScrubbing = true
        sliderScrubPos = it
    }
    val onScrubFinished: () -> Unit = {
        isScrubbing = false
        onSeekTo((sliderScrubPos * safeDur).toInt())
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        val sliderValue = if (isScrubbing) sliderScrubPos else progressRatio
        when (style) {
            AppearancePrefs.SEEK_CAPSULE -> {
                CapsuleMusicSlider(
                    value = sliderValue,
                    onValueChange = onScrub,
                    onValueChangeFinished = onScrubFinished,
                    activeColor = dynamicAccentColor,
                    inactiveColor = inactiveColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            AppearancePrefs.SEEK_CLASSIC -> {
                WavyMusicSlider(
                    value = sliderValue,
                    onValueChange = onScrub,
                    onValueChangeFinished = onScrubFinished,
                    activeTrackColor = dynamicAccentColor,
                    inactiveTrackColor = inactiveColor,
                    thumbColor = contentColor,
                    isPlaying = isPlaying,
                    isWaveEligible = false,
                    trackHeight = 3.dp,
                    thumbRadius = 7.dp,
                    hideInactiveTrackPortion = false,
                    waveAmplitudeWhenPlaying = 0.dp
                )
            }
            AppearancePrefs.SEEK_WAVY -> {
                WavyMusicSlider(
                    value = sliderValue,
                    onValueChange = onScrub,
                    onValueChangeFinished = onScrubFinished,
                    activeTrackColor = dynamicAccentColor,
                    inactiveTrackColor = inactiveColor,
                    thumbColor = contentColor,
                    isPlaying = isPlaying,
                    trackHeight = 4.dp,
                    thumbRadius = 6.dp,
                    waveAmplitudeWhenPlaying = 6.dp,
                    waveLength = 48.dp
                )
            }
            else -> {
                Box(modifier = Modifier.fillMaxWidth()) {
                    WavyMusicSlider(
                        value = sliderValue,
                        onValueChange = onScrub,
                        onValueChangeFinished = onScrubFinished,
                        activeTrackColor = dynamicAccentColor,
                        inactiveTrackColor = inactiveColor,
                        thumbColor = Color.White,
                        isPlaying = isPlaying,
                        trackHeight = 5.dp,
                        thumbRadius = 8.dp,
                        waveAmplitudeWhenPlaying = 10.dp,
                        waveLength = 64.dp
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatDuration(displayPos.toLong()),
                style = MaterialTheme.typography.labelSmall,
                color = contentSecondary
            )
            // Stream codec badge shown in the centre of the transport. The label comes from
            // ExoPlayer's selected audio format and can be hidden in Appearance settings.
            if (showAudioCodec) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(contentColor.copy(alpha = if (isDark) 0.10f else 0.07f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = audioCodec,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = contentSecondary
                    )
                }
            }
            Text(
                text = formatDuration(duration.toLong()),
                style = MaterialTheme.typography.labelSmall,
                color = contentSecondary
            )
        }
    }
}

/**
 * MOONWAVE capsule scrubber: a rounded capsule that thickens while the user drags.
 */
@Composable
internal fun CapsuleMusicSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier
) {
    var isInteracting by remember { mutableStateOf(false) }
    val trackHeight by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isInteracting) 20.dp else 11.dp,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 600f),
        label = "CapsuleHeight"
    )

    Box(
        modifier = modifier
            .height(44.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val fraction = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                    onValueChange(fraction)
                    onValueChangeFinished()
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { isInteracting = true },
                    onDragEnd = {
                        isInteracting = false
                        onValueChangeFinished()
                    },
                    onDragCancel = {
                        isInteracting = false
                        onValueChangeFinished()
                    },
                    onHorizontalDrag = { change, _ ->
                        val fraction = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                        onValueChange(fraction)
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
        ) {
            val radius = size.height / 2f
            drawRoundRect(
                color = inactiveColor,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
            )
            val activeWidth = (size.width * value.coerceIn(0f, 1f)).coerceAtLeast(if (value > 0f) size.height else 0f)
            if (activeWidth > 0f) {
                drawRoundRect(
                    color = activeColor,
                    size = androidx.compose.ui.geometry.Size(activeWidth, size.height),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
                )
            }
        }
    }
}

/**
 * Footer showing song metadata at the bottom of lyrics.
 * Displays song name, artist, album, and lyrics provider — only when values are present.
 * Styled to look "always inactive" with small text and low opacity.
 */
@Composable
internal fun LyricsMetadataFooter(
    currentSong: SongItem?,
    selectedProvider: String?,
    providerResults: Map<String, LyricsResult>,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    if (currentSong == null) return

    // Resolve album from the active provider's matched result
    val activeResult = selectedProvider?.let { providerResults[it] }
    val albumName = activeResult?.matchedAlbum

    val mutedColor = contentColor.copy(alpha = 0.35f)
    val metaStyle = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.3.sp,
        color = mutedColor
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Song title
        if (currentSong.title.isNotBlank()) {
            Text(text = currentSong.title, style = metaStyle, maxLines = 1)
        }
        // Artist
        if (currentSong.artist.isNotBlank() &&
            !currentSong.artist.equals("Unknown Artist", ignoreCase = true)
        ) {
            Text(text = currentSong.artist, style = metaStyle, maxLines = 1)
        }
        // Album (from lyrics provider match)
        if (!albumName.isNullOrBlank()) {
            Text(text = albumName, style = metaStyle, maxLines = 1)
        }
        // Lyrics provider
        if (!selectedProvider.isNullOrBlank()) {
            Text(
                text = "Lyrics by $selectedProvider",
                style = metaStyle,
                maxLines = 1
            )
        }
    }
}

/**
 * Static analog-style film grain. The points are deterministic so the texture never flickers
 * between recompositions; it is only meant to break up gradient banding on large surfaces.
 */
@Composable
internal fun FilmGrainOverlay(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    strength: Float = 0.075f
) {
    Canvas(modifier = modifier) {
        val random = kotlin.random.Random(20261121)
        val count = ((size.width * size.height) / 1400f).toInt().coerceIn(400, 2600)
        repeat(count) {
            drawCircle(
                color = tint.copy(alpha = strength * random.nextFloat()),
                radius = 0.4f + random.nextFloat() * 1.3f,
                center = Offset(
                    x = random.nextFloat() * size.width,
                    y = random.nextFloat() * size.height
                )
            )
        }
    }
}
