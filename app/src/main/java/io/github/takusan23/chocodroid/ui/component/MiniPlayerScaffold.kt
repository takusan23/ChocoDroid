package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

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
 * @param isShowMiniPlayer ボトムシートの状態
 * @param snackbarHostState Snackbar表示用
 * @param miniPlayerState ミニプレイヤーの状態とかをみれる
 * */
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun MiniPlayerScaffold(
    modifier: Modifier = Modifier,
    miniPlayerState: MiniPlayerState = rememberMiniPlayerState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modalBottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
    bottomBar: @Composable () -> Unit = {},
    playerContent: @Composable() (BoxScope.() -> Unit),
    detailContent: @Composable() (BoxScope.() -> Unit),
    bottomSheetContent: @Composable() (ColumnScope.() -> Unit) = {},
    content: @Composable () -> Unit,
) {
    val alpha = kotlin.math.max(miniPlayerState.progress.value, 0f)

    // ボトムシート
    ModalBottomSheetLayout(
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetState = modalBottomSheetState,
        sheetContent = bottomSheetContent,
        content = {
            Scaffold(
                modifier = modifier,
                bottomBar = { if (alpha > 0.01f) Box(modifier = Modifier.alpha(alpha)) { bottomBar() } },
                content = {
                    // topBar / bottomBar 分のPaddingを足す
                    Box(modifier = Modifier.padding(it)) {
                        // 後ろに描画するやつ
                        content()
                        // SnackbarHostがcompose3に未実装なので
                        SnackbarHost(
                            modifier = Modifier.align(alignment = Alignment.BottomCenter),
                            hostState = snackbarHostState
                        )
                    }
                    // 沈んでるので
                    val bottomPadding = if (miniPlayerState.progress.value < 1f) {
                        (alpha * it.calculateBottomPadding().value).dp
                    } else {
                        it.calculateBottomPadding()
                    }
                    MiniPlayerCompose(
                        modifier = Modifier.padding(bottom = bottomPadding), // 沈んでるので
                        state = miniPlayerState,
                        backgroundContent = { },
                        playerContent = playerContent,
                        detailContent = { detailContent() }
                    )
                },
            )
        }
    )
}