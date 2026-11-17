package com.moonwave.music.ui.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moonwave.music.core.model.PlayerLayout
import com.moonwave.music.ui.components.GlassmorphicCard
import com.moonwave.music.ui.theme.CardBorder
import com.moonwave.music.ui.theme.CosmicMagenta
import com.moonwave.music.ui.theme.CosmicViolet
import com.moonwave.music.ui.theme.ElectricBlue
import com.moonwave.music.ui.theme.EmeraldGreen
import com.moonwave.music.ui.theme.MidnightBlack
import com.moonwave.music.ui.theme.MoonlightSilver
import com.moonwave.music.ui.theme.MoonlightSubtext
import com.moonwave.music.ui.theme.MoonlightWhite
import com.moonwave.music.ui.theme.NeonCyan
import com.moonwave.music.ui.theme.SunsetAmber
import com.moonwave.music.ui.theme.SurfaceCard
import com.moonwave.music.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationScreen(
    currentThemeMode: ThemeMode,
    currentAccentHex: String,
    currentLayout: PlayerLayout,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onAccentColorSelect: (String) -> Unit,
    onPlayerLayoutSelect: (PlayerLayout) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColors = listOf(
        Pair("#38BDF8", ElectricBlue),
        Pair("#06B6D4", NeonCyan),
        Pair("#8B5CF6", CosmicViolet),
        Pair("#EC4899", CosmicMagenta),
        Pair("#10B981", EmeraldGreen),
        Pair("#F59E0B", SunsetAmber)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Customization Center",
                        style = MaterialTheme.typography.titleLarge,
                        color = MoonlightWhite
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            // Live Preview Card
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE THEME PREVIEW",
                            style = MaterialTheme.typography.labelSmall,
                            color = MoonlightSubtext
                        )
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = ElectricBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "MOONWAVE Premium",
                        style = MaterialTheme.typography.titleLarge,
                        color = MoonlightWhite
                    )
                    Text(
                        text = "${currentThemeMode.name} • ${currentLayout.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ElectricBlue
                    )
                }
            }

            // Theme Mode Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "THEME MODE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MoonlightSubtext
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.values().forEach { mode ->
                        val isSelected = currentThemeMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { onThemeModeSelect(mode) },
                            label = { Text(mode.name.replace("_", " ")) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBlue.copy(alpha = 0.25f),
                                selectedLabelColor = ElectricBlue
                            )
                        )
                    }
                }
            }

            // Accent Color Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "ACCENT COLOR",
                    style = MaterialTheme.typography.labelSmall,
                    color = MoonlightSubtext
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    accentColors.forEach { (hex, color) ->
                        val isSelected = currentAccentHex.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { onAccentColorSelect(hex) }
                                .then(
                                    if (isSelected) Modifier.border(3.dp, MoonlightWhite, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MidnightBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Player Layout Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "PLAYER LAYOUT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MoonlightSubtext
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlayerLayout.values().forEach { layout ->
                        val isSelected = currentLayout == layout
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ElectricBlue.copy(alpha = 0.15f) else SurfaceCard)
                                .clickable { onPlayerLayoutSelect(layout) }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = layout.name.replace("_", " "),
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isSelected) ElectricBlue else MoonlightWhite
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = ElectricBlue
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
