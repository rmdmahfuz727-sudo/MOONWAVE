package com.moonwave.music.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moonwave.music.core.model.PlaybackState
import com.moonwave.music.ui.navigation.Screen
import com.moonwave.music.ui.navigation.bottomNavScreens
import com.moonwave.music.ui.theme.ElectricBlue
import com.moonwave.music.ui.theme.MidnightBlack
import com.moonwave.music.ui.theme.MoonlightSubtext
import com.moonwave.music.ui.theme.MoonlightWhite
import com.moonwave.music.ui.theme.SurfaceCard

@Composable
fun MoonwaveScaffold(
    currentRoute: String,
    onNavigateToRoute: (String) -> Unit,
    playbackState: PlaybackState,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onMiniPlayerClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    val isBottomNavVisible = bottomNavScreens.any { it.route == currentRoute }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (isBottomNavVisible) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                ) {
                    if (playbackState.currentSong != null) {
                        MiniPlayer(
                            playbackState = playbackState,
                            onPlayPauseClick = onPlayPauseClick,
                            onNextClick = onNextClick,
                            onClick = onMiniPlayerClick
                        )
                    }

                    NavigationBar(
                        containerColor = MidnightBlack.copy(alpha = 0.95f),
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        bottomNavScreens.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { onNavigateToRoute(screen.route) },
                                icon = {
                                    screen.icon?.let { icon ->
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = screen.title
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ElectricBlue,
                                    selectedTextColor = ElectricBlue,
                                    unselectedIconColor = MoonlightSubtext,
                                    unselectedTextColor = MoonlightSubtext,
                                    indicatorColor = SurfaceCard
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content(Modifier.fillMaxSize())
        }
    }
}
