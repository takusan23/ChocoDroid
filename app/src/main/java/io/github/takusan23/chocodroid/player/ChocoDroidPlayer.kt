package io.github.takusan23.chocodroid.player

import android.content.Context
import android.view.SurfaceView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * プレイヤー
 *
 * @param context [Context]
 */
class ChocoDroidPlayer(
    private val context: Context,
    private val onPlayerState: (PlayerState) -> Unit,
    private val onCurrentPositionData: (CurrentPositionData) -> Unit,
    private val onVideoAspectRate: (Float) -> Unit,
    private val onVideoDuration: (Long) -> Unit,
) {

    /** コルーチンスコープ。終了時にキャンセルするため */
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

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
            onVideoAspectRate(videoSize.width / videoSize.height.toFloat())
        }

        /** プレイヤー状況をUIに渡す */
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            onPlayerState(
                when {
                    player.playbackState == Player.STATE_BUFFERING -> PlayerState.Buffering
                    playWhenReady -> PlayerState.Play
                    !playWhenReady -> PlayerState.Pause
                    player.playerError != null -> PlayerState.Error
                    else -> PlayerState.Destroy
                }
            )
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            onVideoDuration(exoPlayer.duration)
        }
    }

    /** ExoPlayer */
    private val exoPlayer by lazy {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    /** 動画時間 */
    val durationMs: Long
        get() = exoPlayer.duration

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

    /** リピートモード */
    var repeatMode: Boolean
        get() = exoPlayer.repeatMode == Player.REPEAT_MODE_ONE
        set(value) {
            exoPlayer.repeatMode = if (value) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_ALL
        }

    init {
        exoPlayer.addListener(playerListener)

        // 再生時間更新用
        scope.launch {
            while (isActive) {
                onCurrentPositionData(
                    CurrentPositionData(
                        currentPositionMs = exoPlayer.currentPosition,
                        bufferingPositionMs = exoPlayer.bufferedPosition
                    )
                )
                delay(100)
            }
        }

        // 設定値の監視を行う
        // リピートモード / 音量調整 など DataStore の値が変わったら更新する
        context.dataStore.data.onEach { setting ->
            repeatMode = setting[SettingKeyObject.PLAYER_REPEAT_MODE] == true
            exoPlayer.volume = setting[SettingKeyObject.PLAYER_VOLUME] ?: 1f
        }.launchIn(scope)
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

    /** プレイヤー破棄 */
    fun destroy() {
        exoPlayer.clearVideoSurface()
        exoPlayer.release()
        scope.coroutineContext.cancelChildren()
    }

}