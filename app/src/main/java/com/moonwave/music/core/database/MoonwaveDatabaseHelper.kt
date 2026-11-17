package com.moonwave.music.core.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.moonwave.music.core.model.LyricsLine
import com.moonwave.music.core.model.LyricsResult
import com.moonwave.music.core.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoonwaveDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "moonwave.db"
        const val DATABASE_VERSION = 1

        const val TABLE_FAVORITES = "favorites"
        const val TABLE_PLAY_HISTORY = "play_history"
        const val TABLE_PLAYLISTS = "playlists"
        const val TABLE_PLAYLIST_SONGS = "playlist_songs"
        const val TABLE_LYRICS_CACHE = "lyrics_cache"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_FAVORITES (song_id INTEGER PRIMARY KEY)")
        db.execSQL("CREATE TABLE $TABLE_PLAY_HISTORY (song_id INTEGER PRIMARY KEY, play_count INTEGER DEFAULT 1, last_played INTEGER)")
        db.execSQL("CREATE TABLE $TABLE_PLAYLISTS (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, created_at INTEGER)")
        db.execSQL("CREATE TABLE $TABLE_PLAYLIST_SONGS (playlist_id INTEGER, song_id INTEGER, sort_order INTEGER, PRIMARY KEY (playlist_id, song_id))")
        db.execSQL("CREATE TABLE $TABLE_LYRICS_CACHE (song_id INTEGER PRIMARY KEY, is_synced INTEGER, plain_lyrics TEXT, raw_lrc TEXT, offset_ms INTEGER, source TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLAY_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYLISTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYLIST_SONGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LYRICS_CACHE")
        onCreate(db)
    }

    // --- FAVORITES ---
    suspend fun getFavoriteSongIds(): Set<Long> = withContext(Dispatchers.IO) {
        val ids = mutableSetOf<Long>()
        readableDatabase.rawQuery("SELECT song_id FROM $TABLE_FAVORITES", null)?.use { cursor ->
            while (cursor.moveToNext()) {
                ids.add(cursor.getLong(0))
            }
        }
        ids
    }

    suspend fun toggleFavorite(songId: Long): Boolean = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM $TABLE_FAVORITES WHERE song_id = ?", arrayOf(songId.toString()))
        val exists = cursor.use { it.moveToFirst() }

        if (exists) {
            db.delete(TABLE_FAVORITES, "song_id = ?", arrayOf(songId.toString()))
            false
        } else {
            val values = ContentValues().apply { put("song_id", songId) }
            db.insert(TABLE_FAVORITES, null, values)
            true
        }
    }

    // --- PLAY HISTORY ---
    suspend fun recordSongPlayed(songId: Long) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.execSQL(
            """
            INSERT INTO $TABLE_PLAY_HISTORY (song_id, play_count, last_played)
            VALUES (?, 1, ?)
            ON CONFLICT(song_id) DO UPDATE SET
                play_count = play_count + 1,
                last_played = ?
            """.trimIndent(),
            arrayOf(songId, System.currentTimeMillis(), System.currentTimeMillis())
        )
    }

    suspend fun getPlayCounts(): Map<Long, Int> = withContext(Dispatchers.IO) {
        val map = mutableMapOf<Long, Int>()
        readableDatabase.rawQuery("SELECT song_id, play_count FROM $TABLE_PLAY_HISTORY", null)?.use { cursor ->
            while (cursor.moveToNext()) {
                map[cursor.getLong(0)] = cursor.getInt(1)
            }
        }
        map
    }

    suspend fun getRecentlyPlayedIds(limit: Int = 50): List<Long> = withContext(Dispatchers.IO) {
        val ids = mutableListOf<Long>()
        readableDatabase.rawQuery(
            "SELECT song_id FROM $TABLE_PLAY_HISTORY ORDER BY last_played DESC LIMIT ?",
            arrayOf(limit.toString())
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                ids.add(cursor.getLong(0))
            }
        }
        ids
    }

    // --- PLAYLISTS ---
    suspend fun getAllPlaylists(): List<Playlist> = withContext(Dispatchers.IO) {
        val playlists = mutableListOf<Playlist>()
        readableDatabase.rawQuery("SELECT id, name, created_at FROM $TABLE_PLAYLISTS ORDER BY name ASC", null)?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val name = cursor.getString(1)
                val createdAt = cursor.getLong(2)
                val songIds = getPlaylistSongIds(id)
                playlists.add(
                    Playlist(
                        id = id,
                        name = name,
                        songIds = songIds,
                        createdAt = createdAt
                    )
                )
            }
        }
        playlists
    }

    private fun getPlaylistSongIds(playlistId: Long): List<Long> {
        val ids = mutableListOf<Long>()
        readableDatabase.rawQuery(
            "SELECT song_id FROM $TABLE_PLAYLIST_SONGS WHERE playlist_id = ? ORDER BY sort_order ASC",
            arrayOf(playlistId.toString())
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                ids.add(cursor.getLong(0))
            }
        }
        return ids
    }

    suspend fun createPlaylist(name: String): Long = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put("name", name)
            put("created_at", System.currentTimeMillis())
        }
        writableDatabase.insert(TABLE_PLAYLISTS, null, values)
    }

    suspend fun deletePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete(TABLE_PLAYLISTS, "id = ?", arrayOf(playlistId.toString()))
        db.delete(TABLE_PLAYLIST_SONGS, "playlist_id = ?", arrayOf(playlistId.toString()))
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val nextOrderCursor = db.rawQuery(
            "SELECT COALESCE(MAX(sort_order), 0) + 1 FROM $TABLE_PLAYLIST_SONGS WHERE playlist_id = ?",
            arrayOf(playlistId.toString())
        )
        val nextOrder = nextOrderCursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }

        val values = ContentValues().apply {
            put("playlist_id", playlistId)
            put("song_id", songId)
            put("sort_order", nextOrder)
        }
        db.insertWithOnConflict(TABLE_PLAYLIST_SONGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        writableDatabase.delete(
            TABLE_PLAYLIST_SONGS,
            "playlist_id = ? AND song_id = ?",
            arrayOf(playlistId.toString(), songId.toString())
        )
    }

    // --- LYRICS CACHE ---
    suspend fun cacheLyrics(songId: Long, result: LyricsResult, rawLrc: String) = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put("song_id", songId)
            put("is_synced", if (result.isSynced) 1 else 0)
            put("plain_lyrics", result.plainLyrics)
            put("raw_lrc", rawLrc)
            put("offset_ms", result.offsetMs)
            put("source", result.source)
        }
        writableDatabase.insertWithOnConflict(TABLE_LYRICS_CACHE, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    suspend fun getCachedLyrics(songId: Long): Pair<LyricsResult, String>? = withContext(Dispatchers.IO) {
        readableDatabase.rawQuery(
            "SELECT is_synced, plain_lyrics, raw_lrc, offset_ms, source FROM $TABLE_LYRICS_CACHE WHERE song_id = ?",
            arrayOf(songId.toString())
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val isSynced = cursor.getInt(0) == 1
                val plainLyrics = cursor.getString(1) ?: ""
                val rawLrc = cursor.getString(2) ?: ""
                val offsetMs = cursor.getLong(3)
                val source = cursor.getString(4) ?: "Cache"

                val lines = if (isSynced && rawLrc.isNotEmpty()) {
                    parseRawLrc(rawLrc)
                } else emptyList()

                Pair(
                    LyricsResult(
                        isSynced = isSynced,
                        lines = lines,
                        plainLyrics = plainLyrics,
                        source = source,
                        offsetMs = offsetMs
                    ),
                    rawLrc
                )
            } else null
        }
    }

    private fun parseRawLrc(rawLrc: String): List<LyricsLine> {
        val lines = mutableListOf<LyricsLine>()
        val regex = Regex("""\[(\d{2}):(\d{2})(?:\.(\d{2,3}))?](.*)""")
        rawLrc.lines().forEach { line ->
            val match = regex.find(line.trim())
            if (match != null) {
                val min = match.groupValues[1].toLongOrNull() ?: 0L
                val sec = match.groupValues[2].toLongOrNull() ?: 0L
                val msStr = match.groupValues[3]
                val ms = when (msStr.length) {
                    2 -> (msStr.toLongOrNull() ?: 0L) * 10
                    3 -> msStr.toLongOrNull() ?: 0L
                    else -> 0L
                }
                val totalMs = (min * 60 + sec) * 1000 + ms
                val text = match.groupValues[4].trim()
                if (text.isNotEmpty()) {
                    lines.add(LyricsLine(timestampMs = totalMs, text = text))
                }
            }
        }
        return lines.sortedBy { it.timestampMs }
    }
}
