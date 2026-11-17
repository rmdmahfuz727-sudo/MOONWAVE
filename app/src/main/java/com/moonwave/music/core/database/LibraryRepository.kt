package com.moonwave.music.core.database

import android.content.Context
import com.moonwave.music.core.model.Album
import com.moonwave.music.core.model.Artist
import com.moonwave.music.core.model.Folder
import com.moonwave.music.core.model.Playlist
import com.moonwave.music.core.model.SmartPlaylistType
import com.moonwave.music.core.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SongSortOrder {
    TITLE_ASC,
    TITLE_DESC,
    ARTIST_ASC,
    ALBUM_ASC,
    DATE_ADDED_DESC,
    DURATION_DESC
}

class LibraryRepository(context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scanner = MusicScanner(context)
    val dbHelper = MoonwaveDatabaseHelper(context)

    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    val allSongs: StateFlow<List<Song>> = _allSongs.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private var currentSortOrder = SongSortOrder.TITLE_ASC

    init {
        refresh()
    }

    fun refresh() {
        scope.launch {
            _isScanning.value = true
            try {
                val rawSongs = scanner.scanMusic()
                val favorites = dbHelper.getFavoriteSongIds()
                val playCounts = dbHelper.getPlayCounts()

                val enrichedSongs = rawSongs.map { song ->
                    song.copy(
                        isFavorite = favorites.contains(song.id),
                        playCount = playCounts[song.id] ?: 0
                    )
                }

                _allSongs.value = applySort(enrichedSongs, currentSortOrder)
                _albums.value = scanner.extractAlbums(enrichedSongs)
                _artists.value = scanner.extractArtists(enrichedSongs)
                _folders.value = scanner.extractFolders(enrichedSongs)

                refreshPlaylistsInternal(enrichedSongs)
            } finally {
                _isScanning.value = false
            }
        }
    }

    private suspend fun refreshPlaylistsInternal(songs: List<Song>) {
        val userPlaylists = dbHelper.getAllPlaylists()
        val smartPlaylists = listOf(
            Playlist(
                id = -1,
                name = "Favorites",
                songIds = songs.filter { it.isFavorite }.map { it.id },
                isSmartPlaylist = true,
                smartType = SmartPlaylistType.FAVORITES
            ),
            Playlist(
                id = -2,
                name = "Most Played",
                songIds = songs.filter { it.playCount > 0 }.sortedByDescending { it.playCount }.map { it.id },
                isSmartPlaylist = true,
                smartType = SmartPlaylistType.MOST_PLAYED
            ),
            Playlist(
                id = -3,
                name = "Recently Added",
                songIds = songs.sortedByDescending { it.dateAdded }.take(50).map { it.id },
                isSmartPlaylist = true,
                smartType = SmartPlaylistType.RECENTLY_ADDED
            )
        )
        _playlists.value = smartPlaylists + userPlaylists
    }

    fun setSortOrder(sortOrder: SongSortOrder) {
        currentSortOrder = sortOrder
        _allSongs.value = applySort(_allSongs.value, sortOrder)
    }

    private fun applySort(songs: List<Song>, sortOrder: SongSortOrder): List<Song> {
        return when (sortOrder) {
            SongSortOrder.TITLE_ASC -> songs.sortedBy { it.title.lowercase() }
            SongSortOrder.TITLE_DESC -> songs.sortedByDescending { it.title.lowercase() }
            SongSortOrder.ARTIST_ASC -> songs.sortedBy { it.artist.lowercase() }
            SongSortOrder.ALBUM_ASC -> songs.sortedBy { it.album.lowercase() }
            SongSortOrder.DATE_ADDED_DESC -> songs.sortedByDescending { it.dateAdded }
            SongSortOrder.DURATION_DESC -> songs.sortedByDescending { it.duration }
        }
    }

    fun toggleFavorite(songId: Long) {
        scope.launch {
            val isFav = dbHelper.toggleFavorite(songId)
            _allSongs.value = _allSongs.value.map {
                if (it.id == songId) it.copy(isFavorite = isFav) else it
            }
            refreshPlaylistsInternal(_allSongs.value)
        }
    }

    fun recordSongPlayed(songId: Long) {
        scope.launch {
            dbHelper.recordSongPlayed(songId)
            _allSongs.value = _allSongs.value.map {
                if (it.id == songId) it.copy(playCount = it.playCount + 1, lastPlayedTimestamp = System.currentTimeMillis()) else it
            }
            refreshPlaylistsInternal(_allSongs.value)
        }
    }

    fun createPlaylist(name: String) {
        scope.launch {
            dbHelper.createPlaylist(name)
            refreshPlaylistsInternal(_allSongs.value)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        scope.launch {
            dbHelper.deletePlaylist(playlistId)
            refreshPlaylistsInternal(_allSongs.value)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        scope.launch {
            dbHelper.addSongToPlaylist(playlistId, songId)
            refreshPlaylistsInternal(_allSongs.value)
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        scope.launch {
            dbHelper.removeSongFromPlaylist(playlistId, songId)
            refreshPlaylistsInternal(_allSongs.value)
        }
    }
}
