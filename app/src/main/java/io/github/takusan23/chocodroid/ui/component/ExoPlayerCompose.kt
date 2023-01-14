package io.github.takusan23.chocodroid.ui.component

import android.view.SurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.github.takusan23.chocodroid.player.ChocoDroidPlayer
import io.github.takusan23.chocodroid.player.PlayerState
import io.github.takusan23.chocodroid.player.VideoMetaData


/**
 * [ChocoDroidPlayer]のUI部分
 *
 * @param videoMetaData 動画データ情報
 * @param playbackState プレイヤーの状態
 * @param surfaceView SurfaceView
 */
@Composable
fun ExoPlayerComposeUI(
    modifier: Modifier = Modifier,
    videoMetaData: VideoMetaData,
    playbackState: PlayerState,
    surfaceView: SurfaceView,
) {
    // 横いっぱいで作る
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.7f),
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
                .aspectRatio(videoMetaData.aspectRate),
            factory = { surfaceView }
        )
        if (playbackState == PlayerState.Buffering) {
            // くるくる
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}