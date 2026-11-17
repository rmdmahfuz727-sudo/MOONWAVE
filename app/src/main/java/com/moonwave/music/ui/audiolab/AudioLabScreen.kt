package com.moonwave.music.ui.audiolab

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moonwave.music.core.model.AudioEffectConfig
import com.moonwave.music.ui.components.GlassmorphicCard
import com.moonwave.music.ui.theme.CosmicViolet
import com.moonwave.music.ui.theme.ElectricBlue
import com.moonwave.music.ui.theme.MidnightBlack
import com.moonwave.music.ui.theme.MoonlightMuted
import com.moonwave.music.ui.theme.MoonlightSilver
import com.moonwave.music.ui.theme.MoonlightSubtext
import com.moonwave.music.ui.theme.MoonlightWhite
import com.moonwave.music.ui.theme.SurfaceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioLabScreen(
    config: AudioEffectConfig,
    onToggleEqualizer: (Boolean) -> Unit,
    onBandLevelChange: (Int, Short) -> Unit,
    onSelectPreset: (Int) -> Unit,
    onToggleBassBoost: (Boolean) -> Unit,
    onBassBoostStrengthChange: (Short) -> Unit,
    onToggleVirtualizer: (Boolean) -> Unit,
    onVirtualizerStrengthChange: (Short) -> Unit,
    onReverbChange: (Boolean, Short) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                        text = "Audio Lab",
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

            // Equalizer Master Card
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Equalizer",
                                tint = ElectricBlue,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Graphic Equalizer",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MoonlightWhite
                                )
                                Text(
                                    text = if (config.isEqualizerEnabled) "Hardware Active" else "Bypassed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (config.isEqualizerEnabled) ElectricBlue else MoonlightMuted
                                )
                            }
                        }

                        Switch(
                            checked = config.isEqualizerEnabled,
                            onCheckedChange = onToggleEqualizer,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MoonlightWhite,
                                checkedTrackColor = ElectricBlue
                            )
                        )
                    }

                    // Presets
                    if (config.presetNames.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "PRESETS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MoonlightSubtext
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            config.presetNames.forEachIndexed { index, name ->
                                val isSelected = config.currentPresetIndex == index
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSelectPreset(index) },
                                    label = { Text(name) },
                                    enabled = config.isEqualizerEnabled,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ElectricBlue.copy(alpha = 0.2f),
                                        selectedLabelColor = ElectricBlue
                                    )
                                )
                            }
                        }
                    }

                    // Frequency Bands
                    if (config.bands.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "FREQUENCY BANDS (dB)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MoonlightSubtext
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        config.bands.forEach { band ->
                            val freqLabel = if (band.centerFreqHz >= 1000) {
                                "${band.centerFreqHz / 1000.0} kHz"
                            } else {
                                "${band.centerFreqHz} Hz"
                            }
                            val dbGain = (band.currentLevelMilliBels / 100.0)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = freqLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MoonlightSilver,
                                    modifier = Modifier.width(68.dp)
                                )

                                Slider(
                                    value = band.currentLevelMilliBels.toFloat(),
                                    onValueChange = { onBandLevelChange(band.index, it.toInt().toShort()) },
                                    valueRange = band.minBandLevel.toFloat()..band.maxBandLevel.toFloat(),
                                    enabled = config.isEqualizerEnabled,
                                    modifier = Modifier.weight(1f),
                                    colors = SliderDefaults.colors(
                                        thumbColor = ElectricBlue,
                                        activeTrackColor = ElectricBlue,
                                        inactiveTrackColor = Color(0x3338BDF8)
                                    )
                                )

                                Text(
                                    text = String.format("%+.1f dB", dbGain),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MoonlightSubtext,
                                    modifier = Modifier.width(60.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Bass Boost & Virtualizer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bass Boost Card
                GlassmorphicCard(modifier = Modifier.weight(1f)) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Waves,
                                contentDescription = "Bass",
                                tint = ElectricBlue
                            )
                            Switch(
                                checked = config.isBassBoostEnabled,
                                onCheckedChange = onToggleBassBoost,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MoonlightWhite,
                                    checkedTrackColor = ElectricBlue
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bass Boost",
                            style = MaterialTheme.typography.titleMedium,
                            color = MoonlightWhite
                        )
                        Text(
                            text = "${config.bassBoostStrength / 10}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlue
                        )
                        Slider(
                            value = config.bassBoostStrength.toFloat(),
                            onValueChange = { onBassBoostStrengthChange(it.toInt().toShort()) },
                            valueRange = 0f..1000f,
                            enabled = config.isBassBoostEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricBlue,
                                activeTrackColor = ElectricBlue
                            )
                        )
                    }
                }

                // Virtualizer Card
                GlassmorphicCard(modifier = Modifier.weight(1f)) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SurroundSound,
                                contentDescription = "3D Surround",
                                tint = CosmicViolet
                            )
                            Switch(
                                checked = config.isVirtualizerEnabled,
                                onCheckedChange = onToggleVirtualizer,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MoonlightWhite,
                                    checkedTrackColor = CosmicViolet
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "3D Virtualizer",
                            style = MaterialTheme.typography.titleMedium,
                            color = MoonlightWhite
                        )
                        Text(
                            text = "${config.virtualizerStrength / 10}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = CosmicViolet
                        )
                        Slider(
                            value = config.virtualizerStrength.toFloat(),
                            onValueChange = { onVirtualizerStrengthChange(it.toInt().toShort()) },
                            valueRange = 0f..1000f,
                            enabled = config.isVirtualizerEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = CosmicViolet,
                                activeTrackColor = CosmicViolet
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
