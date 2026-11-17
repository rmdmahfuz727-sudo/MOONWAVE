package com.moonwave.music.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.moonwave.music.R
import com.moonwave.music.core.database.SongSortOrder
import com.moonwave.music.core.model.Album
import com.moonwave.music.core.model.Artist
import com.moonwave.music.core.model.Folder
import com.moonwave.music.core.model.Song
import com.moonwave.music.ui.components.SongListItem
import com.moonwave.music.ui.theme.ElectricBlue
import com.moonwave.music.ui.theme.MidnightBlack
import com.moonwave.music.ui.theme.MoonlightMuted
import com.moonwave.music.ui.theme.MoonlightSubtext
import com.moonwave.music.ui.theme.MoonlightWhite
import com.moonwave.music.ui.theme.SurfaceCard

@Composable
fun LibraryScreen(
    allSongs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    folders: List<Folder>,
    currentSong: Song?,
    isPlaying: Boolean,
    onSongClick: (Song, List<Song>) -> Unit,
    onFavoriteToggle: (Long) -> Unit,
    onSortChange: (SongSortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Songs", "Albums", "Artists", "Folders")
    var showSortMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Library",
                    style = MaterialTheme.typography.displayMedium,
                    color = MoonlightWhite
                )

                Box {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = MoonlightWhite
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier.background(SurfaceCard)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Title (A-Z)", color = MoonlightWhite) },
                            onClick = {
                                onSortChange(SongSortOrder.TITLE_ASC)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Title (Z-A)", color = MoonlightWhite) },
                            onClick = {
                                onSortChange(SongSortOrder.TITLE_DESC)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Artist", color = MoonlightWhite) },
                            onClick = {
                                onSortChange(SongSortOrder.ARTIST_ASC)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Album", color = MoonlightWhite) },
                            onClick = {
                                onSortChange(SongSortOrder.ALBUM_ASC)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Date Added", color = MoonlightWhite) },
                            onClick = {
                                onSortChange(SongSortOrder.DATE_ADDED_DESC)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Duration", color = MoonlightWhite) },
                            onClick = {
                                onSortChange(SongSortOrder.DURATION_DESC)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = ElectricBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = ElectricBlue
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) ElectricBlue else MoonlightSubtext
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // Songs Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(allSongs) { song ->
                            SongListItem(
                                song = song,
                                isPlaying = isPlaying,
                                isCurrentSong = currentSong?.id == song.id,
                                onSongClick = { onSongClick(song, allSongs) },
                                onFavoriteToggle = { onFavoriteToggle(song.id) },
                                onMoreClick = {}
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
                1 -> {
                    // Albums Tab (Grid)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(albums) { album ->
                            val albumSongs = remember(album, allSongs) {
                                allSongs.filter { it.album.equals(album.title, ignoreCase = true) }
                            }

                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceCard)
                                    .clickable {
                                        if (albumSongs.isNotEmpty()) {
                                            onSongClick(albumSongs.first(), albumSongs)
                                        }
                                    }
                                    .padding(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E2638)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(album.artworkUri)
                                            .crossfade(true)
                                            .placeholder(R.drawable.ic_moonwave_logo)
                                            .error(R.drawable.ic_moonwave_logo)
                                            .build(),
                                        contentDescription = album.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = album.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MoonlightWhite,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${album.artist} • ${album.songCount} songs",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MoonlightSubtext,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Artists Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(artists) { artist ->
                            val artistSongs = remember(artist, allSongs) {
                                allSongs.filter { it.artist.equals(artist.name, ignoreCase = true) }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceCard)
                                    .clickable {
                                        if (artistSongs.isNotEmpty()) {
                                            onSongClick(artistSongs.first(), artistSongs)
                                        }
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(22.dp))
                                        .background(ElectricBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = ElectricBlue
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = artist.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MoonlightWhite
                                    )
                                    Text(
                                        text = "${artist.songCount} songs • ${artist.albumCount} albums",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MoonlightSubtext
                                    )
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
                3 -> {
                    // Folders Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(folders) { folder ->
                            val folderSongs = remember(folder, allSongs) {
                                allSongs.filter { it.path.startsWith(folder.path) }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceCard)
                                    .clickable {
                                        if (folderSongs.isNotEmpty()) {
                                            onSongClick(folderSongs.first(), folderSongs)
                                        }
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF8B5CF6).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = Color(0xFF8B5CF6)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = folder.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MoonlightWhite
                                    )
                                    Text(
                                        text = "${folder.songCount} audio files",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MoonlightSubtext
                                    )
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}
