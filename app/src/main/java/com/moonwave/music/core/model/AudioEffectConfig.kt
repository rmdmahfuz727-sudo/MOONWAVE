package com.moonwave.music.core.model

data class EqualizerBand(
    val index: Int,
    val centerFreqHz: Int,
    val minBandLevel: Short,
    val maxBandLevel: Short,
    val currentLevelMilliBels: Short
)

data class AudioEffectConfig(
    val isEqualizerEnabled: Boolean = false,
    val bands: List<EqualizerBand> = emptyList(),
    val currentPresetIndex: Int = -1,
    val presetNames: List<String> = emptyList(),
    val isBassBoostEnabled: Boolean = false,
    val bassBoostStrength: Short = 0, // 0 - 1000
    val isVirtualizerEnabled: Boolean = false,
    val virtualizerStrength: Short = 0, // 0 - 1000
    val isReverbEnabled: Boolean = false,
    val reverbPreset: Short = 0 // 0 = None, 1 = SmallRoom, 2 = MediumRoom, 3 = LargeRoom, 4 = MediumHall, 5 = LargeHall, 6 = Plate
)
