package com.moonwave.music.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.moonwave.music.R
import com.moonwave.music.model.SongItem

sealed class MochyHeroType {
    object Heart : MochyHeroType()
    object Download : MochyHeroType()
    data class Artwork(val url: String?) : MochyHeroType()
}

enum class MochySortOrder(val displayName: String) {
    DATE_ADDED("date added"),
    TITLE("title"),
    ARTIST("artist"),
    DURATION("duration")
}

/**
 * Mochy Music signature Playlist and Download screen.
 * Displays a warm ambient gradient hero with centered icon or artwork,
 * large bold title, song count & duration subtitle, Play/Shuffle/Search action buttons,
 * sort order dropdown, and edge-to-edge song list.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MochyCollectionScreen(
    title: String,
    heroType: MochyHeroType,
    songs: List<SongItem>,
    currentSong: SongItem?,
    isPlaying: Boolean,
    onBack: () -> Unit,
    onSongClick: (SongItem, List<SongItem>, Int) -> Unit,
    onSongMenuClick: (SongItem) -> Unit,
    onPlayAll: (List<SongItem>) -> Unit,
    onShuffle: (List<SongItem>) -> Unit,
    accentColor: Color = Color(0xFF8B5A2B), // Signature Mochy warm amber/copper
    onOptionsClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var sortOrder by remember { mutableStateOf(MochySortOrder.DATE_ADDED) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    // Filter by search query
    val filteredSongs = remember(songs, searchQuery) {
        if (searchQuery.isBlank()) songs
        else songs.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.artist.contains(searchQuery, ignoreCase = true)
        }
    }

    // Sort songs
    val displaySongs = remember(filteredSongs, sortOrder) {
        when (sortOrder) {
            MochySortOrder.DATE_ADDED -> filteredSongs.sortedByDescending { it.dateAdded }
            MochySortOrder.TITLE -> filteredSongs.sortedBy { it.title.lowercase() }
            MochySortOrder.ARTIST -> filteredSongs.sortedBy { it.artist.lowercase() }
            MochySortOrder.DURATION -> filteredSongs.sortedByDescending { it.duration }
        }
    }

    // Calculate total duration
    val totalDurationMs = remember(songs) { songs.sumOf { it.duration } }
    val formattedDuration = remember(totalDurationMs) {
        val totalSec = totalDurationMs / 1000
        val hours = totalSec / 3600
        val mins = (totalSec % 3600) / 60
        if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
    }

    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Ambient background gradient from the top hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.55f),
                            accentColor.copy(alpha = 0.28f),
                            backgroundColor.copy(alpha = 0.85f),
                            backgroundColor
                        )
                    )
                )
        )

        // Main scrollable content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 150.dp)
        ) {
            // 1. Top Bar + Hero Section
            item(key = "mochy_hero_header") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    // Top Bar: Back & Options buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = onBack,
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (onOptionsClick != null) {
                            Surface(
                                onClick = onOptionsClick,
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.12f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.MoreHoriz,
                                        contentDescription = "Options",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Centered Hero Element
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (heroType) {
                            is MochyHeroType.Heart -> {
                                // Mochy signature outline heart
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_favorite),
                                    contentDescription = "Liked",
                                    tint = Color.White,
                                    modifier = Modifier.size(110.dp)
                                )
                            }
                            is MochyHeroType.Download -> {
                                // Mochy signature outline download icon
                                Icon(
                                    imageVector = Icons.Outlined.FileDownload,
                                    contentDescription = "Downloads",
                                    tint = Color.White,
                                    modifier = Modifier.size(105.dp)
                                )
                            }
                            is MochyHeroType.Artwork -> {
                                // Squircle album artwork
                                val artUrl = heroType.url
                                if (!artUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(artUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(170.dp)
                                            .shadow(16.dp, RoundedCornerShape(26.dp))
                                            .clip(RoundedCornerShape(26.dp))
                                    )
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(26.dp),
                                        color = Color.White.copy(alpha = 0.12f),
                                        modifier = Modifier.size(170.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_library_music),
                                                contentDescription = null,
                                                tint = Color.White.copy(alpha = 0.7f),
                                                modifier = Modifier.size(64.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Title
                    Text(
                        text = title,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Subtitle: "62 songs • 3h 12m"
                    val songCountStr = if (songs.size == 1) "1 song" else "${songs.size} songs"
                    Text(
                        text = "$songCountStr • $formattedDuration",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action buttons row: [▶ Play]  (🔀)  (🔍)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play Pill Button
                        Surface(
                            onClick = { if (displaySongs.isNotEmpty()) onPlayAll(displaySongs) },
                            shape = CircleShape,
                            color = Color(0xFFE5D7C7), // Mochy signature warm tonal pill
                            modifier = Modifier.height(44.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 22.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color(0xFF1D1B16),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Play",
                                    color = Color(0xFF1D1B16),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        // Shuffle Circle Button
                        Surface(
                            onClick = { if (displaySongs.isNotEmpty()) onShuffle(displaySongs.shuffled()) },
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.14f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shuffle,
                                    contentDescription = "Shuffle",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Search Circle Button
                        Surface(
                            onClick = { isSearchActive = !isSearchActive },
                            shape = CircleShape,
                            color = if (isSearchActive) Color.White.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.14f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Optional search input field
                    AnimatedVisibility(
                        visible = isSearchActive,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text("Search in $title...", color = Color.White.copy(alpha = 0.5f))
                                },
                                singleLine = true,
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Clear, null, tint = Color.White)
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Sort dropdown button: "date added ⌵"
                    Box {
                        Row(
                            modifier = Modifier
                                .clickable { sortMenuExpanded = true }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = sortOrder.displayName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Sort",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false }
                        ) {
                            MochySortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            order.displayName,
                                            fontWeight = if (order == sortOrder) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        sortOrder = order
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // 2. Songs List
            if (displaySongs.isEmpty()) {
                item(key = "mochy_empty_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp, horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No matching songs" else "No songs yet",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                itemsIndexed(
                    items = displaySongs,
                    key = { _, song -> "mochy_song_${song.id}_${song.videoId}" }
                ) { index, song ->
                    val isCurrent = currentSong?.id == song.id

                    MochySongItemRow(
                        song = song,
                        isCurrentlyPlaying = isCurrent && isPlaying,
                        onClick = { onSongClick(song, displaySongs, index) },
                        onMenuClick = { onSongMenuClick(song) }
                    )
                }
            }
        }
    }
}

/**
 * Mochy Music signature Song Row:
 * Squircle cover art, bold title, subtitle with artist • duration, downloaded badge, and three-dots menu.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MochySongItemRow(
    song: SongItem,
    isCurrentlyPlaying: Boolean,
    onClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationStr = remember(song.duration) {
        val totalSec = song.duration / 1000
        val mins = totalSec / 60
        val secs = totalSec % 60
        String.format(java.util.Locale.US, "%d:%02d", mins, secs)
    }

    val isDownloaded = remember(song.path, song.data) {
        val raw = song.path ?: song.data ?: ""
        val p = raw.replace('\\', '/').lowercase(java.util.Locale.ROOT)
        p.contains("/moonwave/") || p.contains("/spatialflow/") || p.endsWith(".mp3") || p.endsWith(".m4a")
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onMenuClick
            )
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Squircle Artwork
        val artUri = song.getAlbumArtUri()
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.08f),
            modifier = Modifier.size(48.dp)
        ) {
            if (artUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(artUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = song.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_music_note),
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title + Subtitle
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isCurrentlyPlaying) MaterialTheme.colorScheme.primary else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val artistStr = if (song.artist.isBlank()) "Unknown Artist" else song.artist
                Text(
                    text = "$artistStr • $durationStr",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (isDownloaded) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Downloaded",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // More options button (...)
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "More",
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
