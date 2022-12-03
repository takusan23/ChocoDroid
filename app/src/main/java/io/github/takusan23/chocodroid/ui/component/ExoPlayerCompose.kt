package io.github.takusan23.chocodroid.ui.component

import android.view.SurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.github.takusan23.chocodroid.player.ChocoDroidPlayer
import io.github.takusan23.chocodroid.player.PlaybackStatus


/**
 * [ChocoDroidPlayer]のUI部分
 *
 * @param chocoDroidPlayer Applicationでもらえる
 * @param surfaceView SurfaceView
 */
@Composable
fun ExoPlayerComposeUI(
    chocoDroidPlayer: ChocoDroidPlayer,
    surfaceView: SurfaceView,
) {
    val videoData = chocoDroidPlayer.videoDataFlow.collectAsState()
    val playerStatus = chocoDroidPlayer.playbackStateFlow.collectAsState()

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
                .aspectRatio(videoData.value.aspectRate),
            factory = { surfaceView }
        )
        if (playerStatus.value == PlaybackStatus.Buffering) {
            // くるくる
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}