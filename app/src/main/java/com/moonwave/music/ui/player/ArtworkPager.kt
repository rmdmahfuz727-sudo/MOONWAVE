package com.moonwave.music.ui.player

import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.moonwave.music.R
import com.moonwave.music.model.SongItem
import com.moonwave.music.ui.component.AppearancePrefs
import com.moonwave.music.ui.component.SmoothCornerShape
import com.moonwave.music.ui.component.press3D
import com.moonwave.music.ui.theme.observeKey
import com.moonwave.music.viewmodel.PlayerSharedViewModel

@Composable
fun ArtworkPager(
    viewModel: PlayerSharedViewModel,
    currentSong: SongItem,
    songList: List<SongItem>,
    currentSongIndex: Int,
    context: Context,
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true
) {
    val pagerState = rememberPagerState(
        initialPage = currentSongIndex.coerceAtLeast(0)
    ) {
        songList.size.coerceAtLeast(1)
    }

    // Sync Pager Page with VM when active song changes externally
    LaunchedEffect(currentSongIndex) {
        if (currentSongIndex >= 0 && currentSongIndex < pagerState.pageCount && pagerState.currentPage != currentSongIndex) {
            pagerState.animateScrollToPage(
                page = currentSongIndex,
                animationSpec = tween(durationMillis = 520, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            )
        }
    }

    // Sync VM when swiped in Pager (only when settling/finished scrolling to avoid race conditions)
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress && currentSongIndex >= 0 && pagerState.currentPage != currentSongIndex) {
            viewModel.playSongAtIndex(pagerState.currentPage)
        }
    }

    val prefs = remember(context) {
        context.getSharedPreferences(AppearancePrefs.PREFS, Context.MODE_PRIVATE)
    }
    val transitionStyle by prefs.observeKey(AppearancePrefs.ALBUM_ART_TRANSITION, AppearancePrefs.TRANSITION_SHUFFLE)
    val animatedArt by prefs.observeKey(AppearancePrefs.ANIMATED_ALBUM_ART, true)
    val density = androidx.compose.ui.platform.LocalDensity.current.density

    val kenBurnsTransition = rememberInfiniteTransition(label = "AnimatedAlbumArt")
    val kenBurns by kenBurnsTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AlbumArtKenBurns"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = userScrollEnabled,
            beyondViewportPageCount = 1,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val song = if (page == currentSongIndex) currentSong else (songList.getOrNull(page) ?: currentSong)
            val rawUri = song.getAlbumArtUri()
            val videoId = song.videoId

            val artworkUrl = if (rawUri != null && rawUri.toString().isNotEmpty()) {
                SongItem.enhanceThumbnailUrl(rawUri.toString())
            } else if (!videoId.isNullOrEmpty()) {
                "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
            } else {
                null
            }

            var isError by remember(artworkUrl) { mutableStateOf(false) }

            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                .coerceIn(-1f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val absOffset = kotlin.math.abs(pageOffset)
                        cameraDistance = 22f * density
                        when (transitionStyle) {
                            AppearancePrefs.TRANSITION_FLIP -> {
                                rotationY = pageOffset * 78f
                                scaleX = 1f - absOffset * 0.10f
                                scaleY = 1f - absOffset * 0.10f
                                alpha = 1f - absOffset * 0.35f
                            }
                            AppearancePrefs.TRANSITION_PUSH -> {
                                translationX = pageOffset * 90f
                                scaleX = 1f - absOffset * 0.10f
                                scaleY = 1f - absOffset * 0.10f
                                alpha = 1f - absOffset * 0.25f
                            }
                            AppearancePrefs.TRANSITION_CROSSFADE -> {
                                alpha = (1f - absOffset * 0.9f).coerceIn(0f, 1f)
                                val settle = 0.88f + 0.12f * (1f - absOffset)
                                scaleX = settle
                                scaleY = settle
                            }
                            AppearancePrefs.TRANSITION_SHUFFLE -> {
                                rotationY = pageOffset * 20f
                                rotationZ = pageOffset * 5f
                                translationY = absOffset * 46f
                                scaleX = 1f - absOffset * 0.08f
                                scaleY = 1f - absOffset * 0.08f
                                alpha = 1f - absOffset * 0.30f
                            }
                            else -> Unit
                        }
                        if (animatedArt) {
                            val breathe = 1f + 0.022f * kenBurns
                            scaleX *= breathe
                            scaleY *= breathe
                            translationX += (kenBurns - 0.5f) * 7f
                            translationY += (0.5f - kenBurns) * 5f
                        }
                    }
                    .press3D(maxTilt = 7f, pressedScale = 0.95f)
                    .clip(SmoothCornerShape(26.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!artworkUrl.isNullOrEmpty() && !isError) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(artworkUrl.toUri())
                            .crossfade(320)
                            .precision(coil.size.Precision.EXACT)
                            .build(),
                        contentDescription = null,
                        onState = { state ->
                            if (state is AsyncImagePainter.State.Error) {
                                isError = true
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.colorScheme.surfaceContainerHighest
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_music_note),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(96.dp)
                        )
                    }
                }
            }
        }
    }
}
