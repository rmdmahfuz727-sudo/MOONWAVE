package com.moonwave.music.core.database

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.moonwave.music.core.model.PlayerLayout
import com.moonwave.music.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "moonwave_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_ACCENT_COLOR = stringPreferencesKey("accent_color")
        val KEY_PLAYER_LAYOUT = stringPreferencesKey("player_layout")
        val KEY_CROSSFADE = booleanPreferencesKey("crossfade")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        val name = prefs[KEY_THEME_MODE] ?: ThemeMode.MIDNIGHT.name
        try { ThemeMode.valueOf(name) } catch (e: Exception) { ThemeMode.MIDNIGHT }
    }

    val accentColorHex: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACCENT_COLOR] ?: "#38BDF8"
    }

    val playerLayout: Flow<PlayerLayout> = context.dataStore.data.map { prefs ->
        val name = prefs[KEY_PLAYER_LAYOUT] ?: PlayerLayout.FLAGSHIP.name
        try { PlayerLayout.valueOf(name) } catch (e: Exception) { PlayerLayout.FLAGSHIP }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode.name }
    }

    suspend fun setAccentColor(hex: String) {
        context.dataStore.edit { it[KEY_ACCENT_COLOR] = hex }
    }

    suspend fun setPlayerLayout(layout: PlayerLayout) {
        context.dataStore.edit { it[KEY_PLAYER_LAYOUT] = layout.name }
    }
}
