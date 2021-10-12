package io.github.takusan23.chocodroid.ui.component

import android.content.Context
import android.view.SurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.google.android.exoplayer2.video.VideoSize

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
                .fillMaxWidth()
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
 * */
class ExoPlayerComposeController(
    context: Context,
    isDefaultAutoPlay: Boolean = false,
) {

    /** アスペクト比 */
    val aspectRate = mutableStateOf(1.7f)

    /** 読み込み中？ */
    val isLoading = mutableStateOf(false)

    /** ExoPlayer */
    val exoPlayer = SimpleExoPlayer.Builder(context).build().apply {
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
        }

    }

    init {
        exoPlayer.addListener(playerListener)
    }

    /** URLをExoPlayerにセットする */
    fun setMediaItem(uri: String) {
        val mediaItem = MediaItem.fromUri(uri.toUri())
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    /** 終了時に呼んでください */
    fun destroy() {
        exoPlayer.release()
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