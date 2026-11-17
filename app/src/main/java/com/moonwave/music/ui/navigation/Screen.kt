package com.moonwave.music.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Explore : Screen("explore", "Explore", Icons.Default.Explore)
    object Library : Screen("library", "Library", Icons.Default.LibraryMusic)
    object Playlists : Screen("playlists", "Playlists", Icons.Default.QueueMusic)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    // Fullscreen and secondary destinations
    object FullPlayer : Screen("full_player", "Now Playing")
    object Lyrics : Screen("lyrics", "Lyrics", Icons.Default.Audiotrack)
    object AudioLab : Screen("audio_lab", "Audio Lab", Icons.Default.Equalizer)
    object Customization : Screen("customization", "Customization", Icons.Default.Palette)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object About : Screen("about", "About MOONWAVE")
}

val bottomNavScreens = listOf(
    Screen.Home,
    Screen.Explore,
    Screen.Library,
    Screen.Playlists,
    Screen.Profile
)
