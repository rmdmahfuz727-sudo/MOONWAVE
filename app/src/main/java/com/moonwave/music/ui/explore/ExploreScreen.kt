package com.moonwave.music.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.moonwave.music.core.model.Song
import com.moonwave.music.ui.components.GlassmorphicCard
import com.moonwave.music.ui.theme.CosmicMagenta
import com.moonwave.music.ui.theme.CosmicViolet
import com.moonwave.music.ui.theme.ElectricBlue
import com.moonwave.music.ui.theme.EmeraldGreen
import com.moonwave.music.ui.theme.MidnightBlack
import com.moonwave.music.ui.theme.MoonlightSilver
import com.moonwave.music.ui.theme.MoonlightSubtext
import com.moonwave.music.ui.theme.MoonlightWhite
import com.moonwave.music.ui.theme.NeonCyan
import com.moonwave.music.ui.theme.SunsetAmber
import com.moonwave.music.ui.theme.SurfaceCard

@Composable
fun ExploreScreen(
    allSongs: List<Song>,
    onSongClick: (Song, List<Song>) -> Unit,
    onNavigateToAudioLab: () -> Unit,
    onNavigateToLyrics: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Explore & Hub",
                style = MaterialTheme.typography.displayMedium,
                color = MoonlightWhite
            )
            Text(
                text = "Audio engine capabilities and provider services",
                style = MaterialTheme.typography.bodyMedium,
                color = MoonlightSubtext
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Audio Codecs Card
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Hearing,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HIGH-FIDELITY CODEC SUPPORT",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Lossless Audio Engine",
                        style = MaterialTheme.typography.titleMedium,
                        color = MoonlightWhite
                    )
                    Text(
                        text = "Powered by AndroidX Media3 ExoPlayer with direct hardware bitstream pass-through. Supports FLAC, WAV, ALAC, AAC, MP3, OPUS, and OGG formats with gapless playback.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MoonlightSilver
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Integration Services
            Text(
                text = "INTEGRATION PROVIDERS",
                style = MaterialTheme.typography.labelSmall,
                color = MoonlightSubtext
            )
            Spacer(modifier = Modifier.height(10.dp))

            IntegrationItem(
                title = "LRCLIB Synchronized Lyrics",
                subtitle = "Official open lyrics database with millisecond timestamps",
                icon = Icons.Default.Audiotrack,
                tint = NeonCyan,
                status = "Connected"
            )

            Spacer(modifier = Modifier.height(8.dp))

            IntegrationItem(
                title = "Android MediaStore",
                subtitle = "Real-time automated local audio library scanning and indexer",
                icon = Icons.Default.Sync,
                tint = EmeraldGreen,
                status = "Active"
            )

            Spacer(modifier = Modifier.height(8.dp))

            IntegrationItem(
                title = "Android Audio Effects Engine",
                subtitle = "5/10-Band EQ, Bass Boost, 3D Virtualizer, and Reverb",
                icon = Icons.Default.GraphicEq,
                tint = CosmicViolet,
                status = "Ready"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mood Stations
            Text(
                text = "MOOD PLAYBACK",
                style = MaterialTheme.typography.labelSmall,
                color = MoonlightSubtext
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MoodTile(
                    title = "Cosmic Night",
                    tag = "Deep & Ambient",
                    color = CosmicViolet,
                    onClick = {
                        if (allSongs.isNotEmpty()) {
                            onSongClick(allSongs.shuffled().first(), allSongs)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                MoodTile(
                    title = "Electric Surge",
                    tag = "High Energy",
                    color = ElectricBlue,
                    onClick = {
                        if (allSongs.isNotEmpty()) {
                            onSongClick(allSongs.shuffled().first(), allSongs)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun IntegrationItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    status: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MoonlightWhite
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MoonlightSubtext
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(tint.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                color = tint
            )
        }
    }
}

@Composable
fun MoodTile(
    title: String,
    tag: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MoonlightWhite
            )
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                color = MoonlightSubtext
            )
        }
    }
}
