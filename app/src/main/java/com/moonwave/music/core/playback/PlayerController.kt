package com.moonwave.music.core.playback

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.moonwave.music.core.database.LibraryRepository
import com.moonwave.music.core.model.PlaybackState
import com.moonwave.music.core.model.RepeatMode
import com.moonwave.music.core.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerController(
    private val context: Context,
    private val libraryRepository: LibraryRepository
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var originalQueue: List<Song> = emptyList()
    private var isShuffleEnabled = false
    private var currentRepeatMode = RepeatMode.OFF

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlaybackState { it.copy(isPlaying = isPlaying) }
            if (isPlaying) {
                startProgressTracker()
            } else {
                stopProgressTracker()
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            val isBuffering = state == Player.STATE_BUFFERING
            val isEnded = state == Player.STATE_ENDED

            updatePlaybackState {
                it.copy(
                    isBuffering = isBuffering,
                    durationMs = getPlayer()?.duration?.coerceAtLeast(0) ?: it.durationMs
                )
            }

            if (isEnded) {
                handleTrackEnded()
            }
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            updateProgress()
        }
    }

    init {
        startService()
    }

    private fun startService() {
        val intent = Intent(context, MoonwaveMediaService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun getPlayer() = MoonwaveMediaService.instance?.player

    fun playSong(song: Song, queue: List<Song>) {
        val service = MoonwaveMediaService.instance
        if (service == null) {
            startService()
            scope.launch {
                delay(300)
                playSong(song, queue)
            }
            return
        }

        val player = service.player
        player.removeListener(playerListener)
        player.addListener(playerListener)

        originalQueue = queue
        val effectiveQueue = if (isShuffleEnabled) {
            listOf(song) + (queue.filter { it.id != song.id }.shuffled())
        } else {
            queue
        }
        val targetIndex = effectiveQueue.indexOfFirst { it.id == song.id }.coerceAtLeast(0)

        // Setup ExoPlayer MediaItem
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setAlbumTitle(song.album)
            .setArtworkUri(song.albumArtUri)
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.uri)
            .setMediaMetadata(mediaMetadata)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        libraryRepository.recordSongPlayed(song.id)

        updatePlaybackState {
            it.copy(
                isPlaying = true,
                currentSong = song,
                queue = effectiveQueue,
                queueIndex = targetIndex,
                durationMs = song.duration
            )
        }

        startProgressTracker()
    }

    fun togglePlayPause() {
        val player = getPlayer() ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun seekTo(positionMs: Long) {
        val player = getPlayer() ?: return
        player.seekTo(positionMs)
        updatePlaybackState { it.copy(currentPositionMs = positionMs) }
    }

    fun skipNext() {
        val state = _playbackState.value
        val queue = state.queue
        if (queue.isEmpty()) return

        val nextIndex = state.queueIndex + 1
        if (nextIndex < queue.size) {
            playSong(queue[nextIndex], originalQueue.ifEmpty { queue })
        } else if (currentRepeatMode == RepeatMode.ALL) {
            playSong(queue.first(), originalQueue.ifEmpty { queue })
        }
    }

    fun skipPrevious() {
        val player = getPlayer() ?: return
        if (player.currentPosition > 3000) {
            seekTo(0)
            return
        }

        val state = _playbackState.value
        val queue = state.queue
        if (queue.isEmpty()) return

        val prevIndex = state.queueIndex - 1
        if (prevIndex >= 0) {
            playSong(queue[prevIndex], originalQueue.ifEmpty { queue })
        } else {
            seekTo(0)
        }
    }

    fun toggleShuffle() {
        isShuffleEnabled = !isShuffleEnabled
        val state = _playbackState.value
        val currentSong = state.currentSong

        val newQueue = if (isShuffleEnabled) {
            if (currentSong != null) {
                listOf(currentSong) + (originalQueue.filter { it.id != currentSong.id }.shuffled())
            } else {
                originalQueue.shuffled()
            }
        } else {
            originalQueue
        }

        val newIndex = newQueue.indexOfFirst { it.id == currentSong?.id }.coerceAtLeast(0)
        updatePlaybackState {
            it.copy(
                isShuffle = isShuffleEnabled,
                queue = newQueue,
                queueIndex = newIndex
            )
        }
    }

    fun cycleRepeatMode() {
        currentRepeatMode = when (currentRepeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        updatePlaybackState { it.copy(repeatMode = currentRepeatMode) }
    }

    fun setPlaybackSpeed(speed: Float) {
        MoonwaveMediaService.instance?.setPlaybackSpeed(speed)
        updatePlaybackState { it.copy(playbackSpeed = speed) }
    }

    fun setSleepTimer(minutes: Int) {
        MoonwaveMediaService.instance?.startSleepTimer(minutes)
        updatePlaybackState {
            it.copy(sleepTimerMinutesRemaining = if (minutes > 0) minutes else null)
        }
    }

    fun cancelSleepTimer() {
        MoonwaveMediaService.instance?.cancelSleepTimer()
        updatePlaybackState { it.copy(sleepTimerMinutesRemaining = null) }
    }

    private fun handleTrackEnded() {
        when (currentRepeatMode) {
            RepeatMode.ONE -> {
                seekTo(0)
                getPlayer()?.play()
            }
            RepeatMode.ALL, RepeatMode.OFF -> {
                skipNext()
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                updateProgress()
                delay(300)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        updateProgress()
    }

    private fun updateProgress() {
        val player = getPlayer() ?: return
        val current = player.currentPosition.coerceAtLeast(0)
        val duration = player.duration.coerceAtLeast(0)
        val buffered = player.bufferedPosition.coerceAtLeast(0)
        val sleepRemaining = MoonwaveMediaService.instance?.sleepTimerMinutesRemaining

        updatePlaybackState {
            it.copy(
                currentPositionMs = current,
                durationMs = if (duration > 0) duration else it.durationMs,
                bufferedPositionMs = buffered,
                sleepTimerMinutesRemaining = sleepRemaining
            )
        }
    }

    private inline fun updatePlaybackState(crossinline block: (PlaybackState) -> PlaybackState) {
        _playbackState.value = block(_playbackState.value)
    }
}
