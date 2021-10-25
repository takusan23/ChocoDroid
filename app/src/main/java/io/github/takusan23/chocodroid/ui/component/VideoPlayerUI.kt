package io.github.takusan23.chocodroid.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import io.github.takusan23.internet.data.watchpage.MediaUrlData
import io.github.takusan23.internet.data.watchpage.WatchPageData

/**
 * 動画のプレイヤー部分のUI
 *
 * @param watchPageData 動画情報
 * @param mediaUrlData コンテンツURL
 * @param controller ExoPlayer操作用
 * */
@Composable
fun VideoPlayerUI(
    watchPageData: WatchPageData,
    mediaUrlData: MediaUrlData,
    controller: ExoPlayerComposeController = rememberExoPlayerComposeController(),
) {
    LaunchedEffect(key1 = mediaUrlData, block = {
        // 生放送/オフライン再生とストリーミングで分岐
        if (mediaUrlData.mixTrackUrl != null) {
            controller.setMediaSourceUri(mediaUrlData.mixTrackUrl!!)
        } else {
            // 動画URLを読み込む
            controller.setMediaSourceVideoAudioUriSupportVer(mediaUrlData.videoTrackUrl!!, mediaUrlData.audioTrackUrl!!)
        }
    })
    // SurfaceView設置とリソース開放用意
    ExoPlayerComposeUI(controller = controller)
    DisposableEffect(key1 = Unit, effect = { onDispose { controller.destroy() } })
}