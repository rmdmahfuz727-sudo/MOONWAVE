package com.moonwave.music.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.moonwave.music.R
import com.moonwave.music.core.model.Song
import com.moonwave.music.ui.components.SongListItem
import com.moonwave.music.ui.theme.CosmicViolet
import com.moonwave.music.ui.theme.ElectricBlue
import com.moonwave.music.ui.theme.MidnightBlack
import com.moonwave.music.ui.theme.MoonlightMuted
import com.moonwave.music.ui.theme.MoonlightSilver
import com.moonwave.music.ui.theme.MoonlightSubtext
import com.moonwave.music.ui.theme.MoonlightWhite
import com.moonwave.music.ui.theme.SurfaceCard

@Composable
fun HomeScreen(
    allSongs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    isScanning: Boolean,
    onSongClick: (Song, List<Song>) -> Unit,
    onFavoriteToggle: (Long) -> Unit,
    onRefreshLibrary: () -> Unit,
    onNavigateToAudioLab: () -> Unit,
    onNavigateToCustomization: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val recentlyPlayed = remember(allSongs) {
        allSongs.filter { it.lastPlayedTimestamp > 0 }.sortedByDescending { it.lastPlayedTimestamp }.take(15)
    }

    val mostPlayed = remember(allSongs) {
        allSongs.filter { it.playCount > 0 }.sortedByDescending { it.playCount }.take(10)
    }

    val recentlyAdded = remember(allSongs) {
        allSongs.sortedByDescending { it.dateAdded }.take(10)
    }

    val filteredSongs = remember(searchQuery, allSongs) {
        if (searchQuery.isBlank()) emptyList()
        else allSongs.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.artist.contains(searchQuery, ignoreCase = true) ||
                    it.album.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Logo
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(ElectricBlue, CosmicViolet))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Equalizer,
                                contentDescription = null,
                                tint = MidnightBlack,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MOONWAVE",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MoonlightWhite,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "Premium Audio Experience",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricBlue
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = onNavigateToAudioLab) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Audio Lab",
                                tint = ElectricBlue
                            )
                        }
                        IconButton(onClick = onRefreshLibrary) {
                            if (isScanning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = ElectricBlue,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Library",
                                    tint = MoonlightWhite
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search songs, artists, albums...", color = MoonlightMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MoonlightSubtext
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0x3338BDF8),
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedTextColor = MoonlightWhite,
                        unfocusedTextColor = MoonlightWhite
                    ),
                    singleLine = true
                )
            }

            // Search results if typing
            if (searchQuery.isNotBlank()) {
                item {
                    Text(
                        text = "SEARCH RESULTS (${filteredSongs.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MoonlightSubtext
                    )
                }
                items(filteredSongs) { song ->
                    SongListItem(
                        song = song,
                        isPlaying = isPlaying,
                        isCurrentSong = currentSong?.id == song.id,
                        onSongClick = { onSongClick(song, filteredSongs) },
                        onFavoriteToggle = { onFavoriteToggle(song.id) },
                        onMoreClick = {}
                    )
                }
            } else {
                // Quick Hub row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HubCard(
                            title = "Audio Lab",
                            subtitle = "Hardware EQ & Bass",
                            icon = Icons.Default.Equalizer,
                            color = ElectricBlue,
                            onClick = onNavigateToAudioLab
                        )
                        HubCard(
                            title = "Theme Center",
                            subtitle = "Vinyl & Layouts",
                            icon = Icons.Default.Palette,
                            color = CosmicViolet,
                            onClick = onNavigateToCustomization
                        )
                    }
                }

                // Recently Played horizontal row
                if (recentlyPlayed.isNotEmpty()) {
                    item {
                        Text(
                            text = "RECENTLY PLAYED",
                            style = MaterialTheme.typography.labelSmall,
                            color = MoonlightSubtext,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            recentlyPlayed.forEach { song ->
                                Column(
                                    modifier = Modifier
                                        .width(110.dp)
                                        .clickable { onSongClick(song, recentlyPlayed) }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(110.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF1E2638)),
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
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = song.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MoonlightWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = song.artist,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MoonlightSubtext,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                // Most Played / Quick Picks
                val displayList = if (mostPlayed.isNotEmpty()) mostPlayed else recentlyAdded
                val title = if (mostPlayed.isNotEmpty()) "MOST PLAYED" else "RECENT TRACKS"

                if (displayList.isNotEmpty()) {
                    item {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall,
                            color = MoonlightSubtext,
                            letterSpacing = 1.sp
                        )
                    }

                    items(displayList) { song ->
                        SongListItem(
                            song = song,
                            isPlaying = isPlaying,
                            isCurrentSong = currentSong?.id == song.id,
                            onSongClick = { onSongClick(song, displayList) },
                            onFavoriteToggle = { onFavoriteToggle(song.id) },
                            onMoreClick = {}
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }
}

@Composable
fun HubCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(180.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MoonlightWhite
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MoonlightSubtext,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
