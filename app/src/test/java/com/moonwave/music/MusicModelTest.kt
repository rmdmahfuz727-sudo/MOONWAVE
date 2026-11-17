package com.moonwave.music

import com.moonwave.music.core.model.Album
import com.moonwave.music.core.model.Artist
import com.moonwave.music.core.model.Folder
import com.moonwave.music.core.model.Playlist
import com.moonwave.music.core.model.SmartPlaylistType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class MusicModelTest {

    @Test
    fun testPlaylistDefaults() {
        val playlist = Playlist(
            id = 1L,
            name = "Night Drive",
            songIds = listOf(101L, 102L, 103L)
        )

        assertEquals("Night Drive", playlist.name)
        assertEquals(3, playlist.songCount)
        assertFalse(playlist.isSmartPlaylist)
        assertNull(playlist.smartType)
    }

    @Test
    fun testAlbumAndArtistModels() {
        val album = Album(
            id = 201L,
            title = "Lunar Eclipse",
            artist = "Moonwave",
            songCount = 12,
            year = 2026
        )

        assertEquals("Lunar Eclipse", album.title)
        assertEquals(12, album.songCount)

        val artist = Artist(
            id = 301L,
            name = "Moonwave",
            songCount = 24,
            albumCount = 2
        )

        assertEquals("Moonwave", artist.name)
        assertEquals(24, artist.songCount)

        val folder = Folder(
            path = "/storage/emulated/0/Music",
            name = "Music",
            songCount = 50
        )

        assertEquals("Music", folder.name)
        assertEquals(50, folder.songCount)
    }
}
