package com.moonwave.music.core.database

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.moonwave.music.core.model.Album
import com.moonwave.music.core.model.Artist
import com.moonwave.music.core.model.Folder
import com.moonwave.music.core.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MusicScanner(private val context: Context) {

    suspend fun scanMusic(
        excludedFolders: Set<String> = emptySet(),
        minDurationMs: Long = 10000 // Filter out sound effects under 10s
    ): List<Song> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= ?"
        val selectionArgs = arrayOf(minDurationMs.toString())
        val sortOrder = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

        try {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val trackCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
                val yearCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val title = cursor.getString(titleCol) ?: "Unknown Title"
                    val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                    val album = cursor.getString(albumCol) ?: "Unknown Album"
                    val duration = cursor.getLong(durationCol)
                    val path = cursor.getString(dataCol) ?: ""
                    val albumId = cursor.getLong(albumIdCol)
                    val track = cursor.getInt(trackCol)
                    val year = cursor.getInt(yearCol)
                    val dateAdded = cursor.getLong(dateAddedCol)

                    // Check excluded folders
                    val file = File(path)
                    val parentFolder = file.parent ?: ""
                    if (excludedFolders.any { parentFolder.startsWith(it) }) {
                        continue
                    }

                    val contentUri = ContentUris.withAppendedId(collection, id)
                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    )

                    songs.add(
                        Song(
                            id = id,
                            title = title,
                            artist = if (artist == "<unknown>") "Unknown Artist" else artist,
                            album = if (album == "<unknown>") "Unknown Album" else album,
                            duration = duration,
                            uri = contentUri,
                            path = path,
                            albumArtUri = albumArtUri,
                            trackNumber = track,
                            year = year,
                            dateAdded = dateAdded
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        songs
    }

    fun extractAlbums(songs: List<Song>): List<Album> {
        return songs.groupBy { it.album }
            .map { (albumTitle, songList) ->
                val first = songList.first()
                Album(
                    id = albumTitle.hashCode().toLong(),
                    title = albumTitle,
                    artist = first.artist,
                    artworkUri = first.albumArtUri,
                    songCount = songList.size,
                    year = songList.map { it.year }.maxOrNull() ?: 0
                )
            }.sortedBy { it.title.lowercase() }
    }

    fun extractArtists(songs: List<Song>): List<Artist> {
        return songs.groupBy { it.artist }
            .map { (artistName, songList) ->
                Artist(
                    id = artistName.hashCode().toLong(),
                    name = artistName,
                    songCount = songList.size,
                    albumCount = songList.map { it.album }.distinct().size
                )
            }.sortedBy { it.name.lowercase() }
    }

    fun extractFolders(songs: List<Song>): List<Folder> {
        return songs.groupBy { File(it.path).parent ?: "Root" }
            .map { (folderPath, songList) ->
                Folder(
                    path = folderPath,
                    name = File(folderPath).name.ifEmpty { "Root" },
                    songCount = songList.size
                )
            }.sortedBy { it.name.lowercase() }
    }
}
