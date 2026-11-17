package com.moonwave.music.core.network

import com.moonwave.music.core.database.MoonwaveDatabaseHelper
import com.moonwave.music.core.model.LyricsResult
import com.moonwave.music.core.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LyricsRepository(
    private val dbHelper: MoonwaveDatabaseHelper
) {
    private val lrclibClient = LrclibClient()

    suspend fun getLyrics(song: Song): LyricsResult? = withContext(Dispatchers.IO) {
        // 1. Check local database cache
        val cached = dbHelper.getCachedLyrics(song.id)
        if (cached != null) {
            return@withContext cached.first
        }

        // 2. Fetch from LRCLIB
        val durationSec = (song.duration / 1000).toInt()
        val result = lrclibClient.fetchLyrics(
            trackName = song.title,
            artistName = song.artist,
            albumName = song.album,
            durationSeconds = durationSec
        )

        if (result != null) {
            val rawLrc = if (result.isSynced) {
                result.lines.joinToString("\n") { line ->
                    val min = line.timestampMs / 60000
                    val sec = (line.timestampMs % 60000) / 1000
                    val ms = (line.timestampMs % 1000) / 10
                    String.format("[%02d:%02d.%02d]%s", min, sec, ms, line.text)
                }
            } else ""
            dbHelper.cacheLyrics(song.id, result, rawLrc)
        }

        result
    }

    suspend fun searchLyricsManually(songId: Long, query: String): LyricsResult? = withContext(Dispatchers.IO) {
        val result = lrclibClient.searchLyrics(query)
        if (result != null) {
            val rawLrc = if (result.isSynced) {
                result.lines.joinToString("\n") { line ->
                    val min = line.timestampMs / 60000
                    val sec = (line.timestampMs % 60000) / 1000
                    val ms = (line.timestampMs % 1000) / 10
                    String.format("[%02d:%02d.%02d]%s", min, sec, ms, line.text)
                }
            } else ""
            dbHelper.cacheLyrics(songId, result, rawLrc)
        }
        result
    }

    suspend fun saveOffset(songId: Long, lyricsResult: LyricsResult, newOffsetMs: Long) = withContext(Dispatchers.IO) {
        val updated = lyricsResult.copy(offsetMs = newOffsetMs)
        val rawLrc = if (updated.isSynced) {
            updated.lines.joinToString("\n") { line ->
                val min = line.timestampMs / 60000
                val sec = (line.timestampMs % 60000) / 1000
                val ms = (line.timestampMs % 1000) / 10
                String.format("[%02d:%02d.%02d]%s", min, sec, ms, line.text)
            }
        } else ""
        dbHelper.cacheLyrics(songId, updated, rawLrc)
    }
}
