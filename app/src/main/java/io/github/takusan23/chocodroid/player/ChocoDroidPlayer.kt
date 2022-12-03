package io.github.takusan23.chocodroid.player

import android.content.Context
import android.view.SurfaceView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.TransferListener
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.video.VideoSize
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.internet.tool.SingletonOkHttpClientTool
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * プレイヤー
 * シングルトンにする
 */
class ChocoDroidPlayer(private val context: Context) {

    /** コルーチンスコープ。終了時にキャンセルするため */
    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    /** ExoPlayer。 [createPlayer]から[destroy]まで有効 */
    private lateinit var exoPlayer: ExoPlayer

    private val trackSelector = DefaultTrackSelector(context)
    private val defaultDataSourceFactory = DefaultDataSource.Factory(context) {
        DefaultDataSource(context, SingletonOkHttpClientTool.USER_AGENT, true).apply {
            addTransferListener(transferListener)
        }
    }
    private val transferListener = object : TransferListener {
        override fun onTransferInitializing(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {

        }

        override fun onTransferStart(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {

        }

        override fun onBytesTransferred(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean, bytesTransferred: Int) {

        }

        override fun onTransferEnd(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {

        }
    }
    private val playerListener = object : Player.Listener {
        /** アスペクト比 */
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)
            _videoDataFlow.value = videoDataFlow.value.copy(aspectRate = videoSize.width / videoSize.height.toFloat())
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            _playbackStateFlow.value = when {
                player.playbackState == Player.STATE_BUFFERING -> PlaybackStatus.Buffering
                playWhenReady -> PlaybackStatus.Play
                !playWhenReady -> PlaybackStatus.Pause
                player.playerError != null -> PlaybackStatus.Error
                else -> PlaybackStatus.Destroy
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            _videoDataFlow.value = videoDataFlow.value.copy(durationMs = exoPlayer.duration)
        }
    }

    // UI層にプレイヤーの状態を返す
    private val _playbackStateFlow = MutableStateFlow<PlaybackStatus>(PlaybackStatus.Destroy)
    private val _currentPositionDataFlow = MutableStateFlow<CurrentPositionData?>(null)
    private val _videoDataFlow = MutableStateFlow(VideoData(0, 1.7f)) // copy するので適当な初期値を入れる

    /** 現在の再生状態 */
    val playbackStateFlow = _playbackStateFlow.asStateFlow()

    /** 現在の再生位置とバッファリング位置 */
    val currentPositionDataFlow = _currentPositionDataFlow.asStateFlow()

    /** 動画情報 */
    val videoDataFlow = _videoDataFlow.asStateFlow()

    /** 再生位置 */
    var currentPositionMs: Long
        get() = exoPlayer.currentPosition
        set(value) {
            exoPlayer.seekTo(value)
        }

    /** 再生 / 一時停止 */
    var playWhenReady: Boolean
        get() = exoPlayer.playWhenReady
        set(value) {
            exoPlayer.playWhenReady = value
        }

    /** プレイヤーを作成 */
    fun createPlayer() {
        // 破棄済みのみ
        if (playbackStateFlow.value != PlaybackStatus.Destroy) {
            return
        }
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(playerListener)
            exoPlayer.playWhenReady = true
        }
        // 再生時間更新用
        coroutineScope.launch {
            while (isActive) {
                _currentPositionDataFlow.value = CurrentPositionData(exoPlayer.currentPosition, exoPlayer.bufferedPosition)
                delay(100)
            }
        }
        // 設定値の監視を行う
        // リピートモード / 音量調整 など DataStore の値が変わったら更新する
        context.dataStore.data.onEach { setting ->
            exoPlayer.repeatMode = if (setting[SettingKeyObject.PLAYER_REPEAT_MODE] == true) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_ALL
            exoPlayer.volume = setting[SettingKeyObject.PLAYER_VOLUME] ?: 1f
        }.launchIn(coroutineScope)
    }

    /**
     * [SurfaceView]をセットする
     *
     * @param surfaceView [SurfaceView]
     */
    fun setSurfaceView(surfaceView: SurfaceView) {
        exoPlayer.setVideoSurfaceView(surfaceView)
    }

    /** [SurfaceView]を取り外す */
    fun clearSurface() {
        exoPlayer.clearVideoSurface()
    }

    /**
     * ExoPlayerに動画をセットする。主に生放送用
     *
     * @param uri HLSのManifestへのURLもしくは、DashのManifestへのURL
     * @param isDash Dash形式の場合はtrue
     */
    fun setMediaSourceUri(uri: String, isDash: Boolean) {
        val mediaItem = MediaItem.Builder().apply {
            setUri(uri)
            // DashのURLに拡張子ついてないせいか解読できないみたいなのでヒントを与える
            if (isDash) {
                setMimeType(MimeTypes.APPLICATION_MPD)
            }
        }.build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    /**
     * ExoPlayerに映像と音声をセットする。
     *
     * ビデオトラックとオーディオトラックが別のURLの場合に使ってね。
     *
     * @param audioTrackUri 音声トラックURL
     * @param videoTrackUri 動画トラックURL
     */
    fun setMediaSourceVideoAudioUriSupportVer(videoTrackUri: String, audioTrackUri: String) {
        // シーク前位置
        val prevPos = exoPlayer.currentPosition
        val videoSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(MediaItem.fromUri(videoTrackUri))
        val audioSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(MediaItem.fromUri(audioTrackUri))
        val mergeSource = MergingMediaSource(videoSource, audioSource)
        exoPlayer.setMediaSource(mergeSource)
        exoPlayer.prepare()
        exoPlayer.seekTo(prevPos)
    }

    /** プレイヤー破棄。[exoPlayer]は呼び出してはいけない */
    fun destroy() {
        _playbackStateFlow.value = PlaybackStatus.Destroy
        exoPlayer.clearVideoSurface()
        exoPlayer.release()
        coroutineScope.coroutineContext.cancelChildren()
    }

}