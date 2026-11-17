package com.moonwave.music.core.audiofx

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import com.moonwave.music.core.model.AudioEffectConfig
import com.moonwave.music.core.model.EqualizerBand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioEffectsManager {

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var presetReverb: PresetReverb? = null

    private val _config = MutableStateFlow(AudioEffectConfig())
    val config: StateFlow<AudioEffectConfig> = _config.asStateFlow()

    private var currentAudioSessionId: Int = 0

    fun attachAudioSession(audioSessionId: Int) {
        if (audioSessionId == 0 || audioSessionId == currentAudioSessionId) return
        currentAudioSessionId = audioSessionId

        release()

        try {
            // Equalizer
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = _config.value.isEqualizerEnabled
            }

            // BassBoost
            bassBoost = BassBoost(0, audioSessionId).apply {
                if (strengthSupported) {
                    setStrength(_config.value.bassBoostStrength)
                    enabled = _config.value.isBassBoostEnabled
                }
            }

            // Virtualizer
            virtualizer = Virtualizer(0, audioSessionId).apply {
                if (strengthSupported) {
                    setStrength(_config.value.virtualizerStrength)
                    enabled = _config.value.isVirtualizerEnabled
                }
            }

            // PresetReverb
            presetReverb = PresetReverb(0, audioSessionId).apply {
                preset = _config.value.reverbPreset
                enabled = _config.value.isReverbEnabled
            }

            updateConfigFromHardware()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateConfigFromHardware() {
        val eq = equalizer ?: return
        try {
            val numBands = eq.numberOfBands.toInt()
            val minBandLevel = eq.bandLevelRange[0]
            val maxBandLevel = eq.bandLevelRange[1]

            val bands = (0 until numBands).map { bandIdx ->
                val centerFreq = eq.getCenterFreq(bandIdx.toShort()) / 1000 // Convert mHz to Hz
                val currentLevel = eq.getBandLevel(bandIdx.toShort())
                EqualizerBand(
                    index = bandIdx,
                    centerFreqHz = centerFreq,
                    minBandLevel = minBandLevel,
                    maxBandLevel = maxBandLevel,
                    currentLevelMilliBels = currentLevel
                )
            }

            val numPresets = eq.numberOfPresets.toInt()
            val presetNames = (0 until numPresets).map { eq.getPresetName(it.toShort()) }

            _config.value = _config.value.copy(
                bands = bands,
                presetNames = presetNames,
                currentPresetIndex = eq.currentPreset.toInt()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        try {
            equalizer?.enabled = enabled
            _config.value = _config.value.copy(isEqualizerEnabled = enabled)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBandLevel(bandIndex: Int, levelMilliBels: Short) {
        try {
            equalizer?.setBandLevel(bandIndex.toShort(), levelMilliBels)
            val updatedBands = _config.value.bands.map {
                if (it.index == bandIndex) it.copy(currentLevelMilliBels = levelMilliBels) else it
            }
            _config.value = _config.value.copy(bands = updatedBands, currentPresetIndex = -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun usePreset(presetIndex: Int) {
        try {
            equalizer?.usePreset(presetIndex.toShort())
            updateConfigFromHardware()
            _config.value = _config.value.copy(currentPresetIndex = presetIndex)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBassBoost(enabled: Boolean, strength: Short) {
        try {
            bassBoost?.let {
                if (it.strengthSupported) {
                    it.setStrength(strength)
                    it.enabled = enabled
                }
            }
            _config.value = _config.value.copy(
                isBassBoostEnabled = enabled,
                bassBoostStrength = strength
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setVirtualizer(enabled: Boolean, strength: Short) {
        try {
            virtualizer?.let {
                if (it.strengthSupported) {
                    it.setStrength(strength)
                    it.enabled = enabled
                }
            }
            _config.value = _config.value.copy(
                isVirtualizerEnabled = enabled,
                virtualizerStrength = strength
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setReverb(enabled: Boolean, preset: Short) {
        try {
            presetReverb?.let {
                it.preset = preset
                it.enabled = enabled
            }
            _config.value = _config.value.copy(
                isReverbEnabled = enabled,
                reverbPreset = preset
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        try {
            equalizer?.release()
            bassBoost?.release()
            virtualizer?.release()
            presetReverb?.release()
        } catch (e: Exception) {}
        equalizer = null
        bassBoost = null
        virtualizer = null
        presetReverb = null
    }
}
