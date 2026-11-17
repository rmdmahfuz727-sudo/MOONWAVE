package com.moonwave.music

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moonwave.music.core.model.LyricsResult
import com.moonwave.music.core.model.PlayerLayout
import com.moonwave.music.core.playback.MoonwaveMediaService
import com.moonwave.music.ui.about.AboutScreen
import com.moonwave.music.ui.audiolab.AudioLabScreen
import com.moonwave.music.ui.components.MoonwaveScaffold
import com.moonwave.music.ui.customization.CustomizationScreen
import com.moonwave.music.ui.explore.ExploreScreen
import com.moonwave.music.ui.home.HomeScreen
import com.moonwave.music.ui.library.LibraryScreen
import com.moonwave.music.ui.lyrics.LyricsScreen
import com.moonwave.music.ui.navigation.Screen
import com.moonwave.music.ui.player.PlayerScreen
import com.moonwave.music.ui.playlists.PlaylistsScreen
import com.moonwave.music.ui.theme.ElectricBlue
import com.moonwave.music.ui.theme.MoonwaveTheme
import com.moonwave.music.ui.theme.ThemeMode
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MoonwaveAppContent()
        }
    }

    @Composable
    private fun MoonwaveAppContent() {
        val app = MoonwaveApp.instance
        val libraryRepo = app.libraryRepository
        val playerController = app.playerController
        val lyricsRepo = app.lyricsRepository
        val audioEffectsManager = app.audioEffectsManager
        val prefsRepo = app.preferencesRepository

        val coroutineScope = rememberCoroutineScope()
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

        // DataStore Preferences
        val themeMode by prefsRepo.themeMode.collectAsState(initial = ThemeMode.MIDNIGHT)
        val accentHex by prefsRepo.accentColorHex.collectAsState(initial = "#38BDF8")
        val playerLayout by prefsRepo.playerLayout.collectAsState(initial = PlayerLayout.FLAGSHIP)

        val accentColor = remember(accentHex) {
            try {
                Color(android.graphics.Color.parseColor(accentHex))
            } catch (e: Exception) {
                ElectricBlue
            }
        }

        // Playback State & Library State
        val playbackState by playerController.playbackState.collectAsState()
        val allSongs by libraryRepo.allSongs.collectAsState()
        val albums by libraryRepo.albums.collectAsState()
        val artists by libraryRepo.artists.collectAsState()
        val folders by libraryRepo.folders.collectAsState()
        val playlists by libraryRepo.playlists.collectAsState()
        val isScanning by libraryRepo.isScanning.collectAsState()
        val audioConfig by audioEffectsManager.config.collectAsState()

        // Lyrics State
        var currentLyrics by remember { mutableStateOf<LyricsResult?>(null) }
        var isLyricsLoading by remember { mutableStateOf(false) }

        // Attach Audio Effects session when ExoPlayer starts
        LaunchedEffect(playbackState.isPlaying) {
            MoonwaveMediaService.instance?.player?.audioSessionId?.let { sessionId ->
                if (sessionId != 0) {
                    audioEffectsManager.attachAudioSession(sessionId)
                }
            }
        }

        // Fetch lyrics when song changes
        LaunchedEffect(playbackState.currentSong?.id) {
            val song = playbackState.currentSong
            if (song != null) {
                isLyricsLoading = true
                currentLyrics = lyricsRepo.getLyrics(song)
                isLyricsLoading = false
            } else {
                currentLyrics = null
            }
        }

        // Permission Launcher
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.values.any { it }
            if (granted) {
                libraryRepo.refresh()
            }
        }

        LaunchedEffect(Unit) {
            val permissionsToRequest = mutableListOf<String>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
                }
                if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

            if (permissionsToRequest.isNotEmpty()) {
                permissionLauncher.launch(permissionsToRequest.toTypedArray())
            } else {
                libraryRepo.refresh()
            }
        }

        MoonwaveTheme(
            themeMode = themeMode,
            accentColor = accentColor
        ) {
            MoonwaveScaffold(
                currentRoute = currentRoute,
                onNavigateToRoute = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                playbackState = playbackState,
                onPlayPauseClick = { playerController.togglePlayPause() },
                onNextClick = { playerController.skipNext() },
                onMiniPlayerClick = { navController.navigate(Screen.FullPlayer.route) }
            ) { modifier ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = modifier
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            allSongs = allSongs,
                            currentSong = playbackState.currentSong,
                            isPlaying = playbackState.isPlaying,
                            isScanning = isScanning,
                            onSongClick = { song, queue -> playerController.playSong(song, queue) },
                            onFavoriteToggle = { songId -> libraryRepo.toggleFavorite(songId) },
                            onRefreshLibrary = { libraryRepo.refresh() },
                            onNavigateToAudioLab = { navController.navigate(Screen.AudioLab.route) },
                            onNavigateToCustomization = { navController.navigate(Screen.Customization.route) }
                        )
                    }

                    composable(Screen.Explore.route) {
                        ExploreScreen(
                            allSongs = allSongs,
                            onSongClick = { song, queue -> playerController.playSong(song, queue) },
                            onNavigateToAudioLab = { navController.navigate(Screen.AudioLab.route) },
                            onNavigateToLyrics = { navController.navigate(Screen.Lyrics.route) }
                        )
                    }

                    composable(Screen.Library.route) {
                        LibraryScreen(
                            allSongs = allSongs,
                            albums = albums,
                            artists = artists,
                            folders = folders,
                            currentSong = playbackState.currentSong,
                            isPlaying = playbackState.isPlaying,
                            onSongClick = { song, queue -> playerController.playSong(song, queue) },
                            onFavoriteToggle = { songId -> libraryRepo.toggleFavorite(songId) },
                            onSortChange = { sort -> libraryRepo.setSortOrder(sort) }
                        )
                    }

                    composable(Screen.Playlists.route) {
                        PlaylistsScreen(
                            playlists = playlists,
                            allSongs = allSongs,
                            currentSong = playbackState.currentSong,
                            isPlaying = playbackState.isPlaying,
                            onSongClick = { song, queue -> playerController.playSong(song, queue) },
                            onFavoriteToggle = { songId -> libraryRepo.toggleFavorite(songId) },
                            onCreatePlaylist = { name -> libraryRepo.createPlaylist(name) },
                            onDeletePlaylist = { id -> libraryRepo.deletePlaylist(id) }
                        )
                    }

                    composable(Screen.Profile.route) {
                        AboutScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.FullPlayer.route) {
                        PlayerScreen(
                            playbackState = playbackState,
                            playerLayout = playerLayout,
                            onPlayPauseClick = { playerController.togglePlayPause() },
                            onNextClick = { playerController.skipNext() },
                            onPreviousClick = { playerController.skipPrevious() },
                            onSeekTo = { pos -> playerController.seekTo(pos) },
                            onShuffleToggle = { playerController.toggleShuffle() },
                            onRepeatCycle = { playerController.cycleRepeatMode() },
                            onFavoriteToggle = { id -> libraryRepo.toggleFavorite(id) },
                            onSpeedChange = { speed -> playerController.setPlaybackSpeed(speed) },
                            onSleepTimerSet = { mins -> playerController.setSleepTimer(mins) },
                            onAudioLabClick = { navController.navigate(Screen.AudioLab.route) },
                            onLyricsClick = { navController.navigate(Screen.Lyrics.route) },
                            onCollapseClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Lyrics.route) {
                        LyricsScreen(
                            song = playbackState.currentSong,
                            currentPositionMs = playbackState.currentPositionMs,
                            lyricsResult = currentLyrics,
                            isLoading = isLyricsLoading,
                            onBackClick = { navController.popBackStack() },
                            onSeekTo = { pos -> playerController.seekTo(pos) },
                            onManualSearch = { query ->
                                val song = playbackState.currentSong
                                if (song != null) {
                                    coroutineScope.launch {
                                        isLyricsLoading = true
                                        currentLyrics = lyricsRepo.searchLyricsManually(song.id, query)
                                        isLyricsLoading = false
                                    }
                                }
                            },
                            onOffsetChange = { newOffset ->
                                val song = playbackState.currentSong
                                val lyrics = currentLyrics
                                if (song != null && lyrics != null) {
                                    coroutineScope.launch {
                                        lyricsRepo.saveOffset(song.id, lyrics, newOffset)
                                        currentLyrics = lyrics.copy(offsetMs = newOffset)
                                    }
                                }
                            }
                        )
                    }

                    composable(Screen.AudioLab.route) {
                        AudioLabScreen(
                            config = audioConfig,
                            onToggleEqualizer = { audioEffectsManager.setEqualizerEnabled(it) },
                            onBandLevelChange = { index, level -> audioEffectsManager.setBandLevel(index, level) },
                            onSelectPreset = { audioEffectsManager.usePreset(it) },
                            onToggleBassBoost = { audioEffectsManager.setBassBoost(it, audioConfig.bassBoostStrength) },
                            onBassBoostStrengthChange = { audioEffectsManager.setBassBoost(audioConfig.isBassBoostEnabled, it) },
                            onToggleVirtualizer = { audioEffectsManager.setVirtualizer(it, audioConfig.virtualizerStrength) },
                            onVirtualizerStrengthChange = { audioEffectsManager.setVirtualizer(audioConfig.isVirtualizerEnabled, it) },
                            onReverbChange = { enabled, preset -> audioEffectsManager.setReverb(enabled, preset) },
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Customization.route) {
                        CustomizationScreen(
                            currentThemeMode = themeMode,
                            currentAccentHex = accentHex,
                            currentLayout = playerLayout,
                            onThemeModeSelect = { mode ->
                                coroutineScope.launch { prefsRepo.setThemeMode(mode) }
                            },
                            onAccentColorSelect = { hex ->
                                coroutineScope.launch { prefsRepo.setAccentColor(hex) }
                            },
                            onPlayerLayoutSelect = { layout ->
                                coroutineScope.launch { prefsRepo.setPlayerLayout(layout) }
                            },
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.About.route) {
                        AboutScreen(onBackClick = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
