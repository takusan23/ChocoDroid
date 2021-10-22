package io.github.takusan23.chocodroid.ui.component

import android.content.Context
import android.view.SurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.video.VideoSize
import io.github.takusan23.internet.tool.SingletonOkHttpClientTool
import kotlinx.coroutines.*

/**
 * SurfaceViewとExoPlayer
 *
 * ExoPlayerの操作が外からできるように、[ExoPlayerComposeController]クラスを用意しました。
 * @param controller ExoPlayer操作用
 * */
@Composable
fun ExoPlayerComposeUI(controller: ExoPlayerComposeController) {
    // 横いっぱいで作る
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.7f),
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
                .aspectRatio(controller.aspectRate.value),
            factory = { context ->
                SurfaceView(context).apply {
                    controller.exoPlayer.setVideoSurfaceView(this)
                }
            }
        )
        if (controller.isLoading.value) {
            // くるくる
            CircularProgressIndicator()
        }
    }
}

/**
 * [ExoPlayerComposeUI]操作用クラス
 *
 * hlsを再生するならexoplayer-hlsを依存関係に追加しないとだめだよ！（一敗
 * */
class ExoPlayerComposeController(
    val context: Context,
    isDefaultAutoPlay: Boolean = false,
) {

    /** アスペクト比 */
    val aspectRate = mutableStateOf(1.7f)

    /** 読み込み中？ */
    val isLoading = mutableStateOf(false)

    /** 再生時間（ミリ秒）。定期的に更新されます */
    val currentPosition = mutableStateOf(0L)

    /** 動画時間（ミリ秒）*/
    val duration = mutableStateOf(0L)

    /** コルーチンスコープ。終了時にキャンセルするため */
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val trackSelector = DefaultTrackSelector(context)

    /** ExoPlayer */
    val exoPlayer = SimpleExoPlayer.Builder(context).setTrackSelector(trackSelector).build().apply {
        playWhenReady = isDefaultAutoPlay
    }

    /** イベント受け取り */
    private val playerListener = object : Player.Listener {
        /** アスペクト比 */
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)
            aspectRate.value = videoSize.width / videoSize.height.toFloat()
        }

        /** 読み込み中判定 */
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            isLoading.value = playbackState == Player.STATE_BUFFERING
            duration.value = exoPlayer.duration
        }

    }

    init {
        exoPlayer.addListener(playerListener)
        // 再生時間更新用
        coroutineScope.launch {
            while (isActive) {
                delay(100)
                currentPosition.value = exoPlayer.currentPosition
            }
        }
    }

    /**
     * ExoPlayerに動画をセットする。主に生放送用
     *
     * @param uri URL。HLSも行ける
     * */
    fun setMediaSourceUri(uri: String) {
        val mediaItem = MediaItem.fromUri(uri.toUri())
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    /**
     * ExoPlayerに映像と音声をセットする。
     *
     * ビデオトラックとオーディオトラックが別のURLの場合に使ってね。
     * */
    fun setMediaSourceVideoAudioUriSupportVer(videoTrackUri: String, audioTrackUri: String) {
        val factory = DefaultDataSourceFactory(context, SingletonOkHttpClientTool.USER_AGENT)
        val videoSource = ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(videoTrackUri))
        val audioSource = ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(audioTrackUri))
        val mergeSource = MergingMediaSource(videoSource, audioSource)
        exoPlayer.setMediaSource(mergeSource)
        exoPlayer.prepare()
    }

    /**
     * シークする関数
     * @param position 時間（ミリ秒）
     * */
    fun seek(position: Long) {
        exoPlayer.seekTo(position)
    }

    /** 終了時に呼んでください */
    fun destroy() {
        exoPlayer.release()
        coroutineScope.cancel()
    }

    companion object {

        /**
         * [rememberExoPlayerComposeController]の中で使ってる。よくわからない。
         * */
        fun Saver(
            context: Context,
            isDefaultAutoPlay: Boolean = false,
        ): Saver<ExoPlayerComposeController, *> = Saver(
            save = { isDefaultAutoPlay },
            restore = { restoreIsAutoPlay -> ExoPlayerComposeController(context, restoreIsAutoPlay) }
        )

    }

}

/**
 * [ExoPlayerComposeController]を生成する関数
 *
 * @param isDefaultAutoPlay 自動再生するかどうか
 * */
@Composable
fun rememberExoPlayerComposeController(isDefaultAutoPlay: Boolean = false): ExoPlayerComposeController {
    val context = LocalContext.current
    return rememberSaveable(
        saver = ExoPlayerComposeController.Saver(context, isDefaultAutoPlay),
    ) {
        ExoPlayerComposeController(context, isDefaultAutoPlay)
    }

}