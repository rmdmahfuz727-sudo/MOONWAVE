package com.moonwave.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

data class MoonwaveCategory(
    val title: String,
    val color: Color,
    val backCoverUrl: String? = null,
    val frontCoverUrl: String? = null
)

val DefaultMoonwaveCategories = listOf(
    MoonwaveCategory("Workout", Color(0xFFE67E22), "https://img.youtube.com/vi/kJQP7kiw5Fk/hqdefault.jpg", "https://img.youtube.com/vi/OPf0YbXqDm0/hqdefault.jpg"),
    MoonwaveCategory("1990s", Color(0xFF8E44AD), "https://img.youtube.com/vi/fJ9rUzIMcZQ/hqdefault.jpg", "https://img.youtube.com/vi/4fndeDfaWCg/hqdefault.jpg"),
    MoonwaveCategory("Korean hip-hop", Color(0xFFE74C3C), "https://img.youtube.com/vi/9bZkp7q19f0/hqdefault.jpg", "https://img.youtube.com/vi/2S24h707N3s/hqdefault.jpg"),
    MoonwaveCategory("Reggae & caribbean", Color(0xFF16A085), "https://img.youtube.com/vi/6ZfuNTqbHE8/hqdefault.jpg", "https://img.youtube.com/vi/kffacxfA7G4/hqdefault.jpg"),
    MoonwaveCategory("Feel good", Color(0xFFF39C12), "https://img.youtube.com/vi/ZbZSe6N_BXs/hqdefault.jpg", "https://img.youtube.com/vi/09R8_2nJtjg/hqdefault.jpg"),
    MoonwaveCategory("Rock", Color(0xFF34495E), "https://img.youtube.com/vi/kXYiU_JCYtU/hqdefault.jpg", "https://img.youtube.com/vi/hTWKbfoikeg/hqdefault.jpg"),
    MoonwaveCategory("Pop", Color(0xFFE91E63), "https://img.youtube.com/vi/fRh_vgS2dFE/hqdefault.jpg", "https://img.youtube.com/vi/JGwWNGJdvx8/hqdefault.jpg"),
    MoonwaveCategory("Chill", Color(0xFF27AE60), "https://img.youtube.com/vi/5qap5aO4i9A/hqdefault.jpg", "https://img.youtube.com/vi/RgKAFK5djSk/hqdefault.jpg")
)

val DefaultMoonwaveMoods = listOf(
    "Chill", "Commute", "Energize", "Focus", "Gaming", "Romance", "Sleep", "Party"
)

/**
 * Moonwave Search Top Header with bold title, user profile avatar, and pill search bar.
 */
@Composable
fun MoonwaveSearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onAvatarClick: () -> Unit,
    avatarUrl: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Search",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!avatarUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile",
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
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Moonwave Pill Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search for tracks, albums, lyrics...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 15.sp
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Moonwave Mood & Genre Tile featuring dual tilted overlapping artwork squircles.
 */
@Composable
fun MoonwaveMoodTile(
    category: MoonwaveCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tileBrush = remember(category.color) {
        Brush.linearGradient(
            colors = listOf(
                category.color.copy(alpha = 0.55f),
                category.color.copy(alpha = 0.20f)
            )
        )
    }

    val tileShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .height(78.dp)
            .shadow(elevation = 4.dp, shape = tileShape)
            .clip(tileShape)
            .background(brush = tileBrush)
            .press3D(maxTilt = 3f, pressedScale = 0.97f)
            .clickable(onClick = onClick)
    ) {
        // Tilted dual cover art stack
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp)
                .size(width = 86.dp, height = 78.dp)
        ) {
            if (category.backCoverUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(category.backCoverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (-4).dp, y = 2.dp)
                        .size(44.dp)
                        .graphicsLayer {
                            rotationZ = -24f
                            alpha = 0.85f
                        }
                        .clip(SmoothCornerShape(10.dp))
                )
            }

            if (category.frontCoverUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(category.frontCoverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 10.dp, y = 8.dp)
                        .size(48.dp)
                        .graphicsLayer {
                            rotationZ = -6f
                        }
                        .clip(SmoothCornerShape(12.dp))
                        .shadow(6.dp, SmoothCornerShape(12.dp))
                )
            }
        }

        // Category title
        Text(
            text = category.title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 14.dp, end = 74.dp)
        )
    }
}

/**
 * 2-column "For you" grid with Moonwave styled tiles.
 */
@Composable
fun MoonwaveForYouSection(
    categories: List<MoonwaveCategory> = DefaultMoonwaveCategories,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = "For you",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        categories.chunked(2).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { cat ->
                    MoonwaveMoodTile(
                        category = cat,
                        onClick = { onCategoryClick(cat.title) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Moonwave "Moods & moments" section with pastel pills.
 */
@Composable
fun MoonwaveMoodsAndMomentsSection(
    moods: List<String> = DefaultMoonwaveMoods,
    onMoodClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = "Moods & moments",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        val colors = listOf(
            Color(0xFF3498DB), Color(0xFF9B59B6), Color(0xFFE67E22), Color(0xFF1ABC9C),
            Color(0xFFE74C3C), Color(0xFFF39C12), Color(0xFF2ECC71), Color(0xFF34495E)
        )

        moods.chunked(2).forEachIndexed { index, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEachIndexed { itemIdx, mood ->
                    val color = colors[(index * 2 + itemIdx) % colors.size]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .shadow(2.dp, RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(color.copy(alpha = 0.22f))
                            .press3D(maxTilt = 3f, pressedScale = 0.97f)
                            .clickable { onMoodClick(mood) }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = mood,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
