package dev.stranik.musicapp.data.repository

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dev.stranik.musicapp.data.service.PlaybackService
import dev.stranik.musicapp.domain.model.Track
import dev.stranik.musicapp.domain.repository.PlayerRepository
import dev.stranik.musicapp.presentation.mapper.PlayerState
import dev.stranik.musicapp.presentation.viewmodel.RepeatMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerRepositoryImpl(private val context: Context) : PlayerRepository {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture?.addListener({
            val controller = controller ?: return@addListener

            controller.addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    updateState()
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updateState()
                    if (isPlaying) startProgressUpdate() else stopProgressUpdate()
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    updateState()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    updateState()
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    updateState()
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    updateState()
                }
            })
            updateState()
        }, MoreExecutors.directExecutor())
    }

    private fun updateState() {
        val player = controller ?: return
        val currentMediaItem = player.currentMediaItem
        
        val isLiked = currentMediaItem?.mediaMetadata?.extras?.getBoolean("is_liked") ?: false

        val track = currentMediaItem?.let { item ->
            Track(
                id = item.mediaId,
                title = item.mediaMetadata.title?.toString() ?: "Unknown",
                artistName = item.mediaMetadata.artist?.toString() ?: "Unknown",
                albumTitle = item.mediaMetadata.albumTitle?.toString() ?: "",
                coverUrl = item.mediaMetadata.artworkUri?.toString() ?: "",
                durationFormatted = formatDuration(player.duration),
                isLiked = isLiked
            )
        }

        _playerState.update {
            it.copy(
                currentTrack = track,
                isPlaying = player.isPlaying,
                positionMs = player.currentPosition,
                durationMs = if (player.duration == C.TIME_UNSET) 0L else player.duration,
                repeatMode = when (player.repeatMode) {
                    Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                    Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                    else -> RepeatMode.OFF
                },
                isShuffleEnabled = player.shuffleModeEnabled,
                isLiked = isLiked
            )
        }
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()

        progressJob = scope.launch {
            while (isActive) {
                val player = controller

                if (player != null && player.isPlaying) {
                    _playerState.update { it.copy(positionMs = player.currentPosition) }
                }

                delay(1000)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    @OptIn(UnstableApi::class)
    override suspend fun playTrack(track: Track, hlsUrl: String) {
        val player = controller ?: return
        
        val mediaItem = createMediaItem(track, hlsUrl)
        
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    @OptIn(UnstableApi::class)
    override suspend fun playTracks(tracks: List<Track>, initialTrackIndex: Int, hlsUrls: List<String>) {
        val player = controller ?: return
        
        val mediaItems = tracks.mapIndexed { index, track ->
            createMediaItem(track, hlsUrls[index])
        }

        player.setMediaItems(mediaItems)
        player.seekTo(initialTrackIndex, 0)
        player.prepare()
        player.play()
    }

    @OptIn(UnstableApi::class)
    private fun createMediaItem(track: Track, hlsUrl: String): MediaItem {
        val extras = Bundle().apply {
            putBoolean("is_liked", track.isLiked)
        }

        var mediaMetadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artistName)
            .setAlbumTitle(track.albumTitle)
            .setExtras(extras)

        if (track.coverUrl.isNotEmpty())
            mediaMetadata = mediaMetadata.setArtworkUri(track.coverUrl.toUri())

        return MediaItem.Builder()
            .setMediaId(track.id)
            .setUri(hlsUrl)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .setMediaMetadata(mediaMetadata.build())
            .build()
    }

    override suspend fun pause() {
        controller?.pause()
    }

    override suspend fun resume() {
        controller?.play()
    }

    override suspend fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
    }

    override suspend fun skipNext() {
        controller?.seekToNext()
    }

    override suspend fun skipPrevious() {
        controller?.seekToPrevious()
    }

    override suspend fun setRepeatMode(repeatMode: RepeatMode) {
        val player = controller ?: return
        player.repeatMode = when (repeatMode) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
        }
    }

    override suspend fun setShuffleEnabled(enabled: Boolean) {
        val player = controller ?: return
        player.shuffleModeEnabled = enabled
    }

    @OptIn(UnstableApi::class)
    override suspend fun updateLikeStatus(isLiked: Boolean) {
        val player = controller ?: return
        val currentItem = player.currentMediaItem ?: return
        val currentIndex = player.currentMediaItemIndex
        
        val extras = currentItem.mediaMetadata.extras?.let { Bundle(it) } ?: Bundle()
        extras.putBoolean("is_liked", isLiked)
        
        val newMetadata = currentItem.mediaMetadata.buildUpon()
            .setExtras(extras)
            .build()

        val newMediaItem = currentItem.buildUpon()
            .setMediaMetadata(newMetadata)
            .build()

        player.replaceMediaItem(currentIndex, newMediaItem)
            
        _playerState.update {
            it.copy(
                isLiked = isLiked,
                currentTrack = it.currentTrack?.copy(isLiked = isLiked)
            )
        }
    }

    private fun formatDuration(ms: Long): String {
        if (ms == C.TIME_UNSET) return "0:00"
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}
