package com.moonwave.music.core.model

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

enum class PlayerLayout {
    FLAGSHIP,
    FULL_ARTWORK,
    MINIMAL,
    VINYL,
    GRADIENT,
    LYRICS_FOCUSED,
    COMPACT
}

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentSong: Song? = null,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val isShuffle: Boolean = false,
    val isBuffering: Boolean = false,
    val queue: List<Song> = emptyList(),
    val queueIndex: Int = -1,
    val sleepTimerMinutesRemaining: Int? = null
)
