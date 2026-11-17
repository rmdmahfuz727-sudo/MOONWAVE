package com.moonwave.music.ui.playlists

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moonwave.music.core.model.Playlist
import com.moonwave.music.core.model.SmartPlaylistType
import com.moonwave.music.core.model.Song
import com.moonwave.music.ui.components.SongListItem
import com.moonwave.music.ui.theme.CosmicViolet
import com.moonwave.music.ui.theme.ElectricBlue
import com.moonwave.music.ui.theme.MidnightBlack
import com.moonwave.music.ui.theme.MoonlightMuted
import com.moonwave.music.ui.theme.MoonlightSubtext
import com.moonwave.music.ui.theme.MoonlightWhite
import com.moonwave.music.ui.theme.SurfaceCard

@Composable
fun PlaylistsScreen(
    playlists: List<Playlist>,
    allSongs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onSongClick: (Song, List<Song>) -> Unit,
    onFavoriteToggle: (Long) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    val activePlaylist = selectedPlaylist?.let { sel ->
        playlists.find { it.id == sel.id } ?: sel
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        if (activePlaylist == null) {
            // Playlists Overview
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Playlists",
                        style = MaterialTheme.typography.displayMedium,
                        color = MoonlightWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(playlists) { playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        onClick = { selectedPlaylist = playlist },
                        onDeleteClick = if (!playlist.isSmartPlaylist) {
                            { onDeletePlaylist(playlist.id) }
                        } else null
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Floating create playlist button
            FloatingActionButton(
                onClick = {
                    newPlaylistName = ""
                    showCreateDialog = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = ElectricBlue,
                contentColor = MidnightBlack
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Playlist")
            }
        } else {
            // Playlist Detail View
            val playlistSongs = remember(activePlaylist, allSongs) {
                val songMap = allSongs.associateBy { it.id }
                activePlaylist.songIds.mapNotNull { songMap[it] }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { selectedPlaylist = null }) {
                            Text("< Back", color = ElectricBlue)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text(
                        text = activePlaylist.name,
                        style = MaterialTheme.typography.displayMedium,
                        color = MoonlightWhite
                    )
                    Text(
                        text = "${playlistSongs.size} tracks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MoonlightSubtext
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (playlistSongs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No songs in this playlist",
                                color = MoonlightMuted,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    items(playlistSongs) { song ->
                        SongListItem(
                            song = song,
                            isPlaying = isPlaying,
                            isCurrentSong = currentSong?.id == song.id,
                            onSongClick = { onSongClick(song, playlistSongs) },
                            onFavoriteToggle = { onFavoriteToggle(song.id) },
                            onMoreClick = {}
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Create Playlist Dialog
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("New Playlist", color = MoonlightWhite) },
                text = {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        label = { Text("Playlist Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newPlaylistName.isNotBlank()) {
                                onCreatePlaylist(newPlaylistName.trim())
                                showCreateDialog = false
                            }
                        }
                    ) {
                        Text("Create", color = ElectricBlue)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel", color = MoonlightSubtext)
                    }
                },
                containerColor = SurfaceCard
            )
        }
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val icon = when (playlist.smartType) {
        SmartPlaylistType.FAVORITES -> Icons.Default.Favorite
        SmartPlaylistType.MOST_PLAYED -> Icons.Default.TrendingUp
        SmartPlaylistType.RECENTLY_ADDED -> Icons.Default.NewReleases
        SmartPlaylistType.RECENTLY_PLAYED -> Icons.Default.History
        null -> Icons.Default.QueueMusic
    }

    val iconTint = when (playlist.smartType) {
        SmartPlaylistType.FAVORITES -> Color(0xFFEC4899)
        SmartPlaylistType.MOST_PLAYED -> ElectricBlue
        SmartPlaylistType.RECENTLY_ADDED -> CosmicViolet
        else -> MoonlightWhite
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = playlist.name,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium,
                color = MoonlightWhite
            )
            Text(
                text = "${playlist.songIds.size} songs",
                style = MaterialTheme.typography.bodyMedium,
                color = MoonlightSubtext
            )
        }

        if (onDeleteClick != null) {
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Playlist",
                    tint = MoonlightMuted
                )
            }
        }
    }
}
