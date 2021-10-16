package io.github.takusan23.chocodroid.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import io.github.takusan23.htmlparse.data.watchpage.WatchPageData

/**
 * 動画のプレイヤー部分のUI
 *
 * @param watchPageData 動画情報
 * @param quality 画質。nullなら適当に
 * @param controller ExoPlayer操作用
 * */
@Composable
fun VideoPlayerUI(
    watchPageData: WatchPageData,
    quality: String? = null,
    controller: ExoPlayerComposeController = rememberExoPlayerComposeController()
) {
    LaunchedEffect(key1 = watchPageData, key2 = quality, block = {
        // 動画URLを読み込む
        val mediaData = watchPageData.getMediaUrl("360p")
        controller.setMediaSourceVideoAudioUriSupportVer(mediaData.videoTrackUrl, mediaData.audioTrackUrl)
    })
    // SurfaceView設置とリソース開放用意
    ExoPlayerComposeUI(controller = controller)
    DisposableEffect(key1 = Unit, effect = { onDispose { controller.destroy() } })
}