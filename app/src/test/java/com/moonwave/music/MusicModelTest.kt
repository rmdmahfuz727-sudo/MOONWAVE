package com.moonwave.music

import android.net.Uri
import com.moonwave.music.core.model.Playlist
import com.moonwave.music.core.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class MusicModelTest {

    @Test
    fun testSongCreationAndDefaultValues() {
        val song = Song(
            id = 101L,
            title = "Midnight Echo",
            artist = "Lunar Eclipse",
            album = "Cosmic Journey",
            duration = 180000L,
            uri = Uri.EMPTY,
            path = "/storage/emulated/0/Music/track.mp3"
        )

        assertEquals(101L, song.id)
        assertEquals("Midnight Echo", song.title)
        assertEquals(0, song.playCount)
        assertFalse(song.isFavorite)
    }

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
    }
}
