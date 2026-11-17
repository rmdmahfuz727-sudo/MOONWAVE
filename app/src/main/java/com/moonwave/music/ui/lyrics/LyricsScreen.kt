package com.moonwave.music.ui.lyrics

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moonwave.music.core.model.LyricsLine
import com.moonwave.music.core.model.LyricsResult
import com.moonwave.music.core.model.Song
import com.moonwave.music.ui.theme.ElectricBlue
import com.moonwave.music.ui.theme.MidnightBlack
import com.moonwave.music.ui.theme.MoonlightMuted
import com.moonwave.music.ui.theme.MoonlightSubtext
import com.moonwave.music.ui.theme.MoonlightWhite
import com.moonwave.music.ui.theme.SurfaceCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsScreen(
    song: Song?,
    currentPositionMs: Long,
    lyricsResult: LyricsResult?,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onManualSearch: (String) -> Unit,
    onOffsetChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val adjustedPositionMs = currentPositionMs + (lyricsResult?.offsetMs ?: 0L)

    // Find current active line index
    val activeIndex = remember(lyricsResult, adjustedPositionMs) {
        val lines = lyricsResult?.lines ?: emptyList()
        lines.indexOfLast { it.timestampMs <= adjustedPositionMs }
    }

    // Auto-scroll to active line
    LaunchedEffect(activeIndex) {
        if (activeIndex >= 0) {
            coroutineScope.launch {
                listState.animateScrollToItem(
                    index = (activeIndex - 2).coerceAtLeast(0)
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        // Subtle Gradient glow in background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x3338BDF8),
                            Color(0x1A6366F1),
                            MidnightBlack
                        )
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = song?.title ?: "Lyrics",
                            style = MaterialTheme.typography.titleMedium,
                            color = MoonlightWhite
                        )
                        Text(
                            text = song?.artist ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MoonlightSubtext
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MoonlightWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        searchQuery = "${song?.title ?: ""} ${song?.artist ?: ""}".trim()
                        showSearchDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Lyrics",
                            tint = MoonlightWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Offset Adjustment Bar
            if (lyricsResult?.isSynced == true) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Offset: ${lyricsResult.offsetMs} ms",
                        style = MaterialTheme.typography.labelSmall,
                        color = MoonlightSubtext
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onOffsetChange(lyricsResult.offsetMs - 500) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Minus 500ms",
                                tint = MoonlightWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = { onOffsetChange(lyricsResult.offsetMs + 500) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Plus 500ms",
                                tint = MoonlightWhite
                            )
                        }
                    }
                }
            }

            // Lyrics Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(color = ElectricBlue)
                    }
                    lyricsResult == null || (lyricsResult.lines.isEmpty() && lyricsResult.plainLyrics.isEmpty()) -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "No lyrics found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MoonlightMuted
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(onClick = {
                                searchQuery = "${song?.title ?: ""} ${song?.artist ?: ""}".trim()
                                showSearchDialog = true
                            }) {
                                Text("Search on LRCLIB", color = ElectricBlue)
                            }
                        }
                    }
                    lyricsResult.isSynced -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(60.dp)) }

                            itemsIndexed(lyricsResult.lines) { index, line ->
                                val isActive = index == activeIndex

                                Text(
                                    text = line.text,
                                    fontSize = if (isActive) 22.sp else 16.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isActive) ElectricBlue else MoonlightMuted,
                                    lineHeight = if (isActive) 32.sp else 24.sp,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onSeekTo(line.timestampMs) }
                                        .padding(vertical = 4.dp)
                                )
                            }

                            item { Spacer(modifier = Modifier.height(120.dp)) }
                        }
                    }
                    else -> {
                        // Plain lyrics
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp, vertical = 20.dp)
                        ) {
                            Text(
                                text = lyricsResult.plainLyrics,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MoonlightWhite,
                                lineHeight = 28.sp
                            )
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }

        // Manual Search Dialog
        if (showSearchDialog) {
            AlertDialog(
                onDismissRequest = { showSearchDialog = false },
                title = { Text("Search Lyrics", color = MoonlightWhite) },
                text = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Track / Artist") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showSearchDialog = false
                        onManualSearch(searchQuery)
                    }) {
                        Text("Search", color = ElectricBlue)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSearchDialog = false }) {
                        Text("Cancel", color = MoonlightSubtext)
                    }
                },
                containerColor = SurfaceCard
            )
        }
    }
}
