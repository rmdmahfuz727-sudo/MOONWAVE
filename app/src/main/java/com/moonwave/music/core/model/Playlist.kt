package com.moonwave.music.core.model

import android.net.Uri

data class Playlist(
    val id: Long,
    val name: String,
    val artworkUri: Uri? = null,
    val songIds: List<Long> = emptyList(),
    val songCount: Int = songIds.size,
    val createdAt: Long = System.currentTimeMillis(),
    val isSmartPlaylist: Boolean = false,
    val smartType: SmartPlaylistType? = null
)

enum class SmartPlaylistType {
    FAVORITES,
    MOST_PLAYED,
    RECENTLY_PLAYED,
    RECENTLY_ADDED
}
