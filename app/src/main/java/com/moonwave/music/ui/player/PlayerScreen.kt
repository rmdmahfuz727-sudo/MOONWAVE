package com.moonwave.music.ui.player

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.moonwave.music.R
import com.moonwave.music.core.model.PlaybackState
import com.moonwave.music.core.model.PlayerLayout
import com.moonwave.music.core.model.RepeatMode
import com.moonwave.music.core.model.Song
import com.moonwave.music.core.util.TimeUtils
import com.moonwave.music.ui.components.AnimatedVinyl
import com.moonwave.music.ui.components.WaveformVisualizer
import com.moonwave.music.ui.theme.CosmicViolet
import com.moonwave.music.ui.theme.ElectricBlue
import com.moonwave.music.ui.theme.GlassDark80
import com.moonwave.music.ui.theme.MidnightBlack
import com.moonwave.music.ui.theme.MoonlightMuted
import com.moonwave.music.ui.theme.MoonlightSilver
import com.moonwave.music.ui.theme.MoonlightSubtext
import com.moonwave.music.ui.theme.MoonlightWhite
import com.moonwave.music.ui.theme.PitchBlack
import com.moonwave.music.ui.theme.SurfaceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    playbackState: PlaybackState,
    playerLayout: PlayerLayout,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onShuffleToggle: () -> Unit,
    onRepeatCycle: () -> Unit,
    onFavoriteToggle: (Long) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onSleepTimerSet: (Int) -> Unit,
    onAudioLabClick: () -> Unit,
    onLyricsClick: () -> Unit,
    onCollapseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val song = playbackState.currentSong ?: return
    var isUserSeeking by remember { mutableStateOf(false) }
    var seekSliderValue by remember { mutableFloatStateOf(0f) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }

    val currentPos = if (isUserSeeking) {
        (seekSliderValue * playbackState.durationMs).toLong()
    } else {
        playbackState.currentPositionMs
    }

    val progressFraction = if (playbackState.durationMs > 0) {
        (playbackState.currentPositionMs.toFloat() / playbackState.durationMs).coerceIn(0f, 1f)
    } else 0f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        // Blurred Album Art Background
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.albumArtUri)
                .crossfade(true)
                .placeholder(R.drawable.ic_moonwave_logo)
                .error(R.drawable.ic_moonwave_logo)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(MidnightBlack),
            alpha = when (playerLayout) {
                PlayerLayout.FULL_ARTWORK -> 0.45f
                PlayerLayout.GRADIENT -> 0.15f
                PlayerLayout.MINIMAL -> 0.08f
                else -> 0.25f
            }
        )

        // Gradient Shade Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MidnightBlack.copy(alpha = 0.7f),
                            MidnightBlack.copy(alpha = 0.9f),
                            MidnightBlack
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCollapseClick) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = MoonlightWhite,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLAYING FROM LIBRARY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MoonlightMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = song.album,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MoonlightSilver,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row {
                    IconButton(onClick = onLyricsClick) {
                        Icon(
                            imageVector = Icons.Default.Audiotrack,
                            contentDescription = "Lyrics",
                            tint = MoonlightWhite
                        )
                    }
                    IconButton(onClick = onAudioLabClick) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = "Audio Lab",
                            tint = ElectricBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Artwork Section by Layout
            when (playerLayout) {
                PlayerLayout.VINYL -> {
                    AnimatedVinyl(
                        song = song,
                        isPlaying = playbackState.isPlaying,
                        modifier = Modifier.weight(4f)
                    )
                }
                PlayerLayout.MINIMAL -> {
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        WaveformVisualizer(
                            isPlaying = playbackState.isPlaying,
                            barCount = 24,
                            barWidth = 4.dp,
                            barSpacing = 4.dp,
                            maxHeight = 60.dp
                        )
                    }
                }
                else -> {
                    // Standard / Flagship / Full Artwork / Gradient
                    Box(
                        modifier = Modifier
                            .weight(4f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .shadow(32.dp, shape = RoundedCornerShape(24.dp))
                            .background(Color(0xFF141722)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(song.albumArtUri)
                                .crossfade(true)
                                .placeholder(R.drawable.ic_moonwave_logo)
                                .error(R.drawable.ic_moonwave_logo)
                                .build(),
                            contentDescription = song.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Title & Artist & Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.displayMedium,
                        color = MoonlightWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.titleMedium,
                        color = MoonlightSubtext,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = { onFavoriteToggle(song.id) }) {
                    Icon(
                        imageVector = if (song.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (song.isFavorite) Color(0xFFEC4899) else MoonlightSilver,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seekbar
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = if (isUserSeeking) seekSliderValue else progressFraction,
                    onValueChange = {
                        isUserSeeking = true
                        seekSliderValue = it
                    },
                    onValueChangeFinished = {
                        isUserSeeking = false
                        onSeekTo((seekSliderValue * playbackState.durationMs).toLong())
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricBlue,
                        activeTrackColor = ElectricBlue,
                        inactiveTrackColor = Color(0x3338BDF8)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = TimeUtils.formatDuration(currentPos),
                        style = MaterialTheme.typography.labelSmall,
                        color = MoonlightSubtext
                    )
                    Text(
                        text = TimeUtils.formatDuration(playbackState.durationMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = MoonlightSubtext
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(onClick = onShuffleToggle) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playbackState.isShuffle) ElectricBlue else MoonlightMuted
                    )
                }

                // Previous
                IconButton(
                    onClick = onPreviousClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = MoonlightWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Play / Pause Primary Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ElectricBlue, CosmicViolet)
                            )
                        )
                        .clickable(onClick = onPlayPauseClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                        tint = PitchBlack,
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Next
                IconButton(
                    onClick = onNextClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = MoonlightWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Repeat Mode
                IconButton(onClick = onRepeatCycle) {
                    Icon(
                        imageVector = when (playbackState.repeatMode) {
                            RepeatMode.ONE -> Icons.Default.RepeatOne
                            else -> Icons.Default.Repeat
                        },
                        contentDescription = "Repeat",
                        tint = if (playbackState.repeatMode != RepeatMode.OFF) ElectricBlue else MoonlightMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Quick Actions: Speed, Sleep Timer, Layout Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed Selector
                TextButton(onClick = { showSpeedDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Speed",
                        tint = MoonlightSubtext,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${playbackState.playbackSpeed}x",
                        style = MaterialTheme.typography.labelSmall,
                        color = MoonlightSilver
                    )
                }

                // Sleep Timer
                TextButton(onClick = { showSleepTimerDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Sleep Timer",
                        tint = if (playbackState.sleepTimerMinutesRemaining != null) ElectricBlue else MoonlightSubtext,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (playbackState.sleepTimerMinutesRemaining != null) {
                            "${playbackState.sleepTimerMinutesRemaining}m"
                        } else "Off",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (playbackState.sleepTimerMinutesRemaining != null) ElectricBlue else MoonlightSilver
                    )
                }
            }
        }

        // Sleep Timer Dialog
        if (showSleepTimerDialog) {
            AlertDialog(
                onDismissRequest = { showSleepTimerDialog = false },
                title = { Text("Sleep Timer", color = MoonlightWhite) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0, 15, 30, 45, 60).forEach { mins ->
                            TextButton(
                                onClick = {
                                    onSleepTimerSet(mins)
                                    showSleepTimerDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (mins == 0) "Turn Off" else "$mins Minutes",
                                    color = if (playbackState.sleepTimerMinutesRemaining == mins) ElectricBlue else MoonlightWhite
                                )
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showSleepTimerDialog = false }) {
                        Text("Cancel", color = MoonlightSubtext)
                    }
                },
                containerColor = SurfaceCard
            )
        }

        // Speed Dialog
        if (showSpeedDialog) {
            AlertDialog(
                onDismissRequest = { showSpeedDialog = false },
                title = { Text("Playback Speed", color = MoonlightWhite) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { spd ->
                            TextButton(
                                onClick = {
                                    onSpeedChange(spd)
                                    showSpeedDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${spd}x",
                                    color = if (playbackState.playbackSpeed == spd) ElectricBlue else MoonlightWhite
                                )
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showSpeedDialog = false }) {
                        Text("Cancel", color = MoonlightSubtext)
                    }
                },
                containerColor = SurfaceCard
            )
        }
    }
}
