package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

/**
 * ミニプレイヤーとScaffoldを連動させたやつ
 *
 * タイトルバーというかTopBarはそれぞれの画面で...
 *
 * BottomNavigationを引っ込めたりするのに使ってる
 *
 * @param modifier 一応
 * @param bottomBar ナビゲーションバー
 * @param content 動画一覧とか、プレイヤーの後ろに置くコンポーネント
 * @param detailContent 動画説明部分
 * @param playerContent 動画再生部分
 * @param scaffoldState Snackbar表示用
 * @param miniPlayerState ミニプレイヤーの状態とかをみれる
 * */
@Composable
fun MiniPlayerScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState,
    miniPlayerState: MiniPlayerState = rememberMiniPlayerState(),
    isShowMiniPlayer: Boolean = true,
    bottomBar: @Composable () -> Unit = {},
    playerContent: @Composable (BoxScope.() -> Unit),
    detailContent: @Composable (BoxScope.() -> Unit),
    content: @Composable () -> Unit,
) {
    val alpha = miniPlayerState.progress.value
    val bottomBarPadding = remember { mutableStateOf(PaddingValues()) }
    Box {
        Scaffold(
            modifier = modifier,
            scaffoldState = scaffoldState,
            bottomBar = { Box(modifier = Modifier.alpha(alpha)) { bottomBar() } },
            content = {
                // topBar / bottomBar 分のPaddingを足す
                bottomBarPadding.value = it
                Box(modifier = Modifier.padding(it)) {
                    content()
                    MiniPlayerCompose(
                        state = miniPlayerState,
                        backgroundContent = { },
                        playerContent = playerContent,
                        detailContent = detailContent
                    )
                }
            }
        )
    }

}