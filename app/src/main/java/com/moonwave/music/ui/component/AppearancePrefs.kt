package com.moonwave.music.ui.component

/**
 * Preference keys and stable option ids shared by the Appearance settings screen and the
 * components that honour those settings (navigation bar, player, artwork pager, ...).
 *
 * All values live in the same "AppSettings" SharedPreferences file the rest of the app uses so
 * changes are picked up immediately through [com.moonwave.music.ui.theme.observeKey].
 */
object AppearancePrefs {
    const val PREFS = "AppSettings"

    // Player artwork
    const val ALBUM_ART_TRANSITION = "album_art_transition"
    const val ANIMATED_ALBUM_ART = "animated_album_art"
    const val LYRICS_FILM_GRAIN = "lyrics_film_grain"

    // Navigation
    const val NAVIGATION_STYLE = "navigation_style"
    const val SLIDE_NAV_ON_SCROLL = "slide_nav_on_scroll"

    // Surfaces
    const val FROSTED_GLASS = "frosted_glass_blur"
    const val SOLID_STATE_SURFACES = "solid_state_surfaces"
    const val TAB_SWITCHING_BLUR = "tab_switching_blur"

    // Display
    const val FORCE_HIGH_REFRESH_RATE = "force_high_refresh_rate"

    // Now playing
    const val PLAYER_THEME = "player_theme"
    const val SEEK_BAR_STYLE = "seek_bar_style"
    const val PLAYER_GRADIENT_BLUR = "player_gradient_blur"
    const val SHOW_AUDIO_CODEC = "show_audio_codec"

    const val NAV_FLOATING = "floating"
    const val NAV_DOCKED = "docked"

    const val PLAYER_FLUID = "fluid"
    const val PLAYER_STATIC = "static"

    const val SEEK_CAPSULE = "capsule"
    const val SEEK_WAVY = "wavy"
    const val SEEK_CLASSIC = "classic"
    const val SEEK_EXPRESSIVE = "expressive"

    const val TRANSITION_SHUFFLE = "shuffle"
    const val TRANSITION_FLIP = "flip"
    const val TRANSITION_PUSH = "push"
    const val TRANSITION_CROSSFADE = "crossfade"
    const val TRANSITION_SLIDE = "slide"
}
