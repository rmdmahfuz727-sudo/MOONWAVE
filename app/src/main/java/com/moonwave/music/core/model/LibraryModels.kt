package com.moonwave.music.core.model

import android.net.Uri

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val artworkUri: Uri? = null,
    val songCount: Int = 0,
    val year: Int = 0
)

data class Artist(
    val id: Long,
    val name: String,
    val songCount: Int = 0,
    val albumCount: Int = 0
)

data class Folder(
    val path: String,
    val name: String,
    val songCount: Int = 0
)
