package com.moonwave.music.core.model

data class LyricsLine(
    val timestampMs: Long,
    val text: String,
    val translation: String? = null
)

data class LyricsResult(
    val isSynced: Boolean,
    val lines: List<LyricsLine> = emptyList(),
    val plainLyrics: String = "",
    val source: String = "Local",
    val offsetMs: Long = 0L
)
