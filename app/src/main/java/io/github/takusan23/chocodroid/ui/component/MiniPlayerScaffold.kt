package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import kotlin.math.min

/**
 * ミニプレイヤーとScaffoldを連動させたやつ
 *
 * BottomNavigationを引っ込めたりするのに使ってる
 *
 * @param modifier 一応
 * @param topBar タイトルバー
 * @param bottomBar ナビゲーションバー
 * @param content 動画一覧とか、プレイヤーの後ろに置くコンポーネント
 * @param detailContent 動画説明部分
 * @param playerContent 動画再生部分
 * @param snackbarHost SnackbarHost
 * @param miniPlayerState ミニプレイヤーの状態とかをみれる
 * */
@Composable
fun MiniPlayerScaffold(
    modifier: Modifier = Modifier,
    isShowMiniPlayer: Boolean = true,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    playerContent: @Composable (BoxScope.() -> Unit),
    detailContent: @Composable (BoxScope.() -> Unit),
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    miniPlayerState: MiniPlayerState = rememberMiniPlayerState(),
    content: @Composable () -> Unit,
) {
    // AppbarもBottomNavBarも56.dpらしい
    val progress = if (isShowMiniPlayer) miniPlayerState.progress.value else 1f

    Scaffold(
        modifier = modifier,
        topBar = {
            var topBarHeight = 0f
            BoxWithConstraints(
                modifier = if (topBarHeight != 0f) Modifier.height((topBarHeight * progress).dp) else Modifier,
                content = {
                    if (topBarHeight == 0f) {
                        topBarHeight = maxHeight.value
                    }
                    topBar()
                }
            )
        },
        bottomBar = {
            val bottomBarHeight = remember { mutableStateOf(0.dp) }
            BoxWithConstraints(
                // modifier = if (bottomBarHeight.value != 0.dp) Modifier.height(bottomBarHeight.value * progress) else Modifier,
                content = {
                    bottomBarHeight.value = maxHeight
                    bottomBar()
                }
            )
        },
        snackbarHost = snackbarHost,
        content = {
            // topBar / bottomBar 分のPaddingを足す
            Box(modifier = Modifier.padding(it)) {
                content()
                if (isShowMiniPlayer) {
                    MiniPlayerCompose(
                        state = miniPlayerState,
                        backgroundContent = { },
                        playerContent = playerContent,
                        detailContent = detailContent
                    )
                }
            }
        }
    )

}