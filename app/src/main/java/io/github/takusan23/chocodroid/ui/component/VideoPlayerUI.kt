package io.github.takusan23.chocodroid.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import io.github.takusan23.htmlparse.html.data.WatchPageData
import io.github.takusan23.htmlparse.html.data.WatchPageJSONResponseData

/**
 * 動画のプレイヤー部分のUI
 *
 * @param watchPageJSONResponseData 動画情報
 * @param exoPlayerComposeController ExoPlayer操作用
 * */
@Composable
fun VideoPlayerUI(
    watchPageData: WatchPageData,
    exoPlayerComposeController: ExoPlayerComposeController = rememberExoPlayerComposeController()
) {
    LaunchedEffect(key1 = watchPageData, block = {
        // 動画URLを読み込む
        val signatureCipher = watchPageData.watchPageJSONResponseData.streamingData.formats[0].signatureCipher
        val url = if (signatureCipher == null) {
            watchPageData.watchPageJSONResponseData.streamingData.formats[0].url!!
        } else {
            watchPageData.decryptURL(signatureCipher)
        }
        exoPlayerComposeController.setMediaItem(url)
    })
    // SurfaceView設置とリソース開放用意
    ExoPlayerComposeUI(controller = exoPlayerComposeController)
    DisposableEffect(key1 = Unit, effect = { onDispose { exoPlayerComposeController.destroy() } })
}