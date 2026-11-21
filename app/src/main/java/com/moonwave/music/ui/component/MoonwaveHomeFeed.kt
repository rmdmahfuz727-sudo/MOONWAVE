@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.moonwave.music.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.moonwave.music.R
import com.moonwave.music.data.innertube.OnlineSong
import com.moonwave.music.ui.theme.observeKey
import kotlinx.coroutines.delay
import kotlin.math.abs

/** The kinds of cards the home hero rotates through, mirroring the reference home carousel. */
enum class HeroCardKind { CONTINUE, GACHA, ON_REPEAT, DISCOVER, PLAYLIST }

data class HeroPick(
    val kind: HeroCardKind,
    val label: String,
    val title: String,
    val subtitle: String = "",
    val thumbnailUrl: String? = null,
    val song: OnlineSong? = null
)

private val HeroAdvanceMillis = 5600L
private val HeroSlideEasing = CubicBezierEasing(0.32f, 0f, 0.15f, 1f)
private val HeroPillFill = Color.White.copy(alpha = 0.68f)
private val HeroPillContent = Color(0xFF202020)

/**
 * Large, auto-advancing hero carousel used at the top of the home feed.
 *
 * Cards wipe horizontally with a soft parallax on their artwork and stop rotating as soon as the
 * list is scrolled away (matching the reference home's behaviour). The gacha card rolls a reel
 * before handing control back to the caller.
 */
@Composable
fun MoonwaveHeroCarousel(
    picks: List<HeroPick>,
    onPick: (HeroPick) -> Unit,
    scrollProvider: () -> Float,
    modifier: Modifier = Modifier
) {
    if (picks.isEmpty()) return

    val configuration = LocalConfiguration.current
    val heroHeight = (configuration.screenHeightDp * 0.48f).dp.coerceIn(320.dp, 480.dp)
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { picks.size })

    // The same Appearance preference that blurs tab switches also softens the large hero card
    // wipe, so switching tabs into Home and swiping cards share one coherent blur transition.
    val heroContext = LocalContext.current
    val heroPrefs = remember(heroContext) {
        heroContext.getSharedPreferences(AppearancePrefs.PREFS, android.content.Context.MODE_PRIVATE)
    }
    val cardBlurEnabled by heroPrefs.observeKey(AppearancePrefs.TAB_SWITCHING_BLUR, false)

    LaunchedEffect(pagerState, picks.size) {
        while (picks.size > 1) {
            delay(HeroAdvanceMillis)
            if (pagerState.isScrollInProgress || scrollProvider() > 0.08f) continue
            val next = (pagerState.currentPage + 1) % picks.size
            pagerState.animateScrollToPage(
                page = next,
                animationSpec = tween(durationMillis = 1050, easing = HeroSlideEasing)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heroHeight)
            .homeScrollParallax(scrollProvider, fadeDistance = 660f, translationFactor = 0.18f)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { page ->
            val pick = picks[page]
            val rawOffset = (page - pagerState.currentPage) - pagerState.currentPageOffsetFraction
            val offset = rawOffset.coerceIn(-1.2f, 1.2f)
            HeroPage(
                pick = pick,
                offsetProvider = { offset },
                blurEnabled = cardBlurEnabled,
                onPick = { onPick(pick) }
            )
        }

        // Page dots, subtle and only when there is more than one card.
        if (picks.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                repeat(picks.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (selected) 16.dp else 5.dp, 5.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) Color.White.copy(alpha = 0.9f)
                                else Color.White.copy(alpha = 0.35f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroPage(
    pick: HeroPick,
    offsetProvider: () -> Float,
    blurEnabled: Boolean = false,
    onPick: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var isRolling by remember { mutableStateOf(false) }
    val transition = rememberInfiniteTransition(label = "HeroGlow")
    val glow by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Reverse),
        label = "HeroGlowPhase"
    )

    val cardOffset = offsetProvider()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .graphicsLayer {
                val o = cardOffset
                alpha = (1f - abs(o) * 0.35f).coerceIn(0f, 1f)
                translationX = o * 46f
                val scale = 1f - abs(o) * 0.06f
                scaleX = scale
                scaleY = scale
            }
            .blur(if (blurEnabled) (abs(cardOffset) * 9f).dp else 0.dp)
            .clip(SmoothCornerShape(28.dp))
            .shadow(10.dp, SmoothCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.35f))
    ) {
        if (pick.kind == HeroCardKind.GACHA) {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFE85973),
                            Color(0xFF9B51E0).copy(alpha = 0.92f),
                            Color(0xFF2C3E50)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(900f, 700f)
                    )
                )
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_dice),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.16f + 0.06f * glow),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 24.dp, end = 24.dp)
                    .size(120.dp)
            )
        } else if (pick.thumbnailUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(pick.thumbnailUrl)
                    .crossfade(350)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                )
            )
        }

        // Scrim keeps the label legible over any artwork.
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.18f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.42f),
                        Color.Black.copy(alpha = 0.86f)
                    )
                )
            )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 22.dp, end = 22.dp, bottom = 34.dp)
        ) {
            Text(
                text = pick.label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White.copy(alpha = 0.74f),
                letterSpacing = 1.1.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(5.dp))
            AnimatedContent(
                targetState = pick.title,
                transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(160)) },
                label = "HeroTitle"
            ) { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (pick.subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = pick.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.82f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(13.dp))
            Row(
                modifier = Modifier
                    .height(34.dp)
                    .clip(RoundedCornerShape(50))
                    .background(HeroPillFill)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (pick.kind == HeroCardKind.GACHA && !isRolling) {
                            isRolling = true
                        }
                        onPick()
                    }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(
                        id = if (pick.kind == HeroCardKind.GACHA) R.drawable.ic_dice else R.drawable.ic_play
                    ),
                    contentDescription = null,
                    tint = HeroPillContent,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        isRolling -> "Rolling..."
                        pick.kind == HeroCardKind.GACHA -> "Roll"
                        else -> "Play"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = HeroPillContent
                )
            }
        }
    }

    LaunchedEffect(isRolling) {
        if (isRolling) {
            delay(620)
            isRolling = false
        }
    }
}

/** "Most Played" style numbered two-column song grid from the reference home. */
@Composable
fun MoonwaveRankedSongSection(
    songs: List<OnlineSong>,
    onSongClick: (OnlineSong) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Most Played"
) {
    if (songs.isEmpty()) return
    val rows = songs.take(6).chunked(2)
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rows.forEachIndexed { rowIndex, row ->
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    row.forEach { song ->
                        val rank = rowIndex * 2 + row.indexOf(song) + 1
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSongClick(song) }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rank.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(song.thumbnailUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            )
                            Spacer(modifier = Modifier.width(9.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = song.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Compact "History" song rows from the reference home. */
@Composable
fun MoonwaveHistorySection(
    songs: List<OnlineSong>,
    onSongClick: (OnlineSong) -> Unit,
    onHeaderClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (songs.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onHeaderClick() }
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "History",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            songs.take(5).forEach { song ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSongClick(song) }
                        .padding(vertical = 7.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(song.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/** "From your playlists" card from the reference home, rendered from a playlist-style section. */
@Composable
fun MoonwavePlaylistFeatureCard(
    title: String,
    subtitle: String,
    coverUrls: List<String?>,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .height(150.dp)
            .clip(SmoothCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.tertiaryContainer
                    )
                )
            )
            .clickable { onPlay() }
    ) {
        Row {
            coverUrls.take(3).forEach { url ->
                AsyncImage(
                    model = ImageRequest.Builder(context).data(url).crossfade(true).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.horizontalGradient(
                    listOf(Color.Black.copy(alpha = 0.70f), Color.Transparent)
                )
            )
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(18.dp)
        ) {
            Text(
                text = subtitle.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.85f))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = HeroPillContent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Play",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = HeroPillContent
                )
            }
        }
    }
}
