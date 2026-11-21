package com.moonwave.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.moonwave.music.R

/**
 * Stacked overlapping angled artwork covers tucked into the bottom-right corner of a card.
 */
@Composable
fun MoonwaveCoverStack(
    covers: List<String?>,
    modifier: Modifier = Modifier,
    tile: Dp = 46.dp,
    round: Boolean = false,
    frontArt: (@Composable (Modifier) -> Unit)? = null
) {
    val shown = remember(covers) { covers.filterNotNull().take(2) }
    if (shown.isEmpty() && frontArt == null) return
    val back = if (frontArt != null) shown.firstOrNull() else shown.getOrNull(1)
    val corner = if (round) tile / 2 else tile * 0.22f

    Box(modifier = modifier.size(width = tile * 1.55f, height = tile)) {
        // Back cover tucked behind with -9 deg tilt
        if (back != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(tile)
                    .graphicsLayer {
                        alpha = 0.55f
                        rotationZ = if (round) 0f else -9f
                    }
                    .clip(if (round) CircleShape else RoundedCornerShape(corner))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(back)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Front cover with 7 deg tilt
        val frontModifier = Modifier
            .align(Alignment.CenterEnd)
            .size(tile)
            .graphicsLayer { rotationZ = if (round) 0f else 7f }

        if (frontArt != null) {
            frontArt(frontModifier.clip(if (round) CircleShape else RoundedCornerShape(corner)))
        } else if (shown.isNotEmpty()) {
            Box(
                modifier = frontModifier
                    .clip(if (round) CircleShape else RoundedCornerShape(corner))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .shadow(elevation = 4.dp, shape = if (round) CircleShape else RoundedCornerShape(corner))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(shown[0])
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Moonwave Category Card with bold count and tucked corner cover stack.
 */
@Composable
fun MoonwaveLibraryCategoryCard(
    title: String,
    count: Int,
    covers: List<String?>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    roundCovers: Boolean = false
) {
    val cardShape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .height(84.dp)
            .shadow(elevation = 2.dp, shape = cardShape)
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .press3D(maxTilt = 3f, pressedScale = 0.97f)
            .clickable(onClick = onClick)
    ) {
        // Covers in bottom-right corner
        MoonwaveCoverStack(
            covers = covers,
            round = roundCovers,
            tile = 46.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 6.dp, y = 8.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 14.dp, top = 10.dp, end = 12.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = count.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

/**
 * Moonwave Wide Card for "Recently added".
 */
@Composable
fun MoonwaveLibraryWideCard(
    title: String,
    count: Int,
    covers: List<String?>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    frontArt: (@Composable (Modifier) -> Unit)? = null
) {
    val cardShape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(98.dp)
            .shadow(elevation = 2.dp, shape = cardShape)
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .press3D(maxTilt = 3f, pressedScale = 0.98f)
            .clickable(onClick = onClick)
    ) {
        MoonwaveCoverStack(
            covers = covers,
            tile = 58.dp,
            frontArt = frontArt,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 8.dp, y = 10.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 12.dp, end = 100.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = count.toString(),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

/**
 * Moonwave "Import your music here!" card.
 */
@Composable
fun MoonwaveImportBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bannerShape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = bannerShape)
            .clip(bannerShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .press3D(maxTilt = 2f, pressedScale = 0.985f)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Platform badges
                val badgeShape = RoundedCornerShape(12.dp)
                listOf(
                    Pair("YT", Color(0xFFFF0000)),
                    Pair("SP", Color(0xFF1DB954)),
                    Pair("AM", Color(0xFFFC3C44)),
                    Pair("TD", Color(0xFF000000))
                ).forEach { (name, color) ->
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(badgeShape)
                            .background(color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Import your music here!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Moonwave Library Top Header with bold "Library" and avatar.
 */
@Composable
fun MoonwaveLibraryTopBar(
    avatarUrl: String?,
    onAvatarClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Library",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onHistoryClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = "History",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (!avatarUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Account Settings",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onAvatarClick() },
                    contentScale = ContentScale.Crop
                )
            } else {
                IconButton(onClick = onAvatarClick) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Account Settings",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

/**
 * Moonwave "Search your library" search bar.
 */
@Composable
fun MoonwaveLibrarySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .height(48.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = "Search your library",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Moonwave Filter Chip Row: ["All", "Local", "Downloads", "Cached"].
 */
@Composable
fun MoonwaveLibraryFilterChips(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("All", "Local", "Downloads", "Cached")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { tab ->
            val isSelected = selectedTab == tab
            val chipShape = RoundedCornerShape(50)

            Box(
                modifier = Modifier
                    .clip(chipShape)
                    .background(
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        }
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tab,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
