package io.github.takusan23.chocodroid.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import io.github.takusan23.htmlparse.data.WatchPageResponseData

/**
 * 動画のプレイヤー部分のUI
 *
 * @param watchPageResponseData 動画情報
 * @param exoPlayerComposeController ExoPlayer操作用
 * */
@Composable
fun VideoPlayerUI(
    watchPageResponseData: WatchPageResponseData,
    exoPlayerComposeController: ExoPlayerComposeController = rememberExoPlayerComposeController()
) {
    LaunchedEffect(key1 = watchPageResponseData, block = {
        // データを読み込む
        val url = watchPageResponseData.streamingData.formats[0].url ?: watchPageResponseData.streamingData.formats[0].decryptionMagic()!!
        exoPlayerComposeController.setMediaItem(url)
    })
    // SurfaceView設置とリソース開放用意
    ExoPlayerComposeUI(controller = exoPlayerComposeController)
    DisposableEffect(key1 = Unit, effect = { onDispose { exoPlayerComposeController.destroy() } })
}