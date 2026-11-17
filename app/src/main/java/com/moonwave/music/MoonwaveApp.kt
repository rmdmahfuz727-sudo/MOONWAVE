package com.moonwave.music

import android.app.Application
import com.moonwave.music.core.audiofx.AudioEffectsManager
import com.moonwave.music.core.database.LibraryRepository
import com.moonwave.music.core.database.UserPreferencesRepository
import com.moonwave.music.core.network.LyricsRepository
import com.moonwave.music.core.playback.PlayerController

class MoonwaveApp : Application() {

    companion object {
        lateinit var instance: MoonwaveApp
            private set
    }

    lateinit var libraryRepository: LibraryRepository
        private set

    lateinit var playerController: PlayerController
        private set

    lateinit var lyricsRepository: LyricsRepository
        private set

    lateinit var audioEffectsManager: AudioEffectsManager
        private set

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        libraryRepository = LibraryRepository(this)
        playerController = PlayerController(this, libraryRepository)
        lyricsRepository = LyricsRepository(libraryRepository.dbHelper)
        audioEffectsManager = AudioEffectsManager()
        preferencesRepository = UserPreferencesRepository(this)
    }
}
