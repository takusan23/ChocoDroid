package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.takusan23.chocodroid.ui.component.*
import io.github.takusan23.chocodroid.ui.component.tool.DisableSleepComposeApp
import io.github.takusan23.chocodroid.ui.theme.ChocoDroidTheme
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel
import java.net.URLDecoder

/**
 * MainActivityに置くやつ
 *
 * Jetpack Composeのスタート地点
 *
 * @param viewModel ViewModel
 * */
@Composable
fun ChocoDroidMainScreen(viewModel: MainScreenViewModel) {
    ChocoDroidTheme {
        Surface {
            Scaffold(
                topBar = { HomeScreenSearchBox(onEnterVideoId = { viewModel.loadWatchPage(it) }) },
                bottomBar = { HomeScreenBottomNavigation() },
                content = {
                    // 将来的に
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "工事中")
                    }
                }
            )

            // 動画プレイヤー
            val miniPlayerState = rememberMiniPlayerState(onStateChange = {
                if (it == MiniPlayerStateValue.End) {
                    viewModel.closePlayer()
                }
            })
            ChocoDroidVideoPlayerScreen(
                viewModel = viewModel,
                miniPlayerState = miniPlayerState,
                backScreen = {


                }
            )
        }
    }
}

/**
 * 動画再生画面
 *
 * @param viewModel ViewModel
 * @param backScreen プレイヤーの背後に置くUI
 * */
@Composable
fun ChocoDroidVideoPlayerScreen(
    viewModel: MainScreenViewModel,
    miniPlayerState: MiniPlayerState,
    backScreen: @Composable () -> Unit,
) {
    // 動画情報
    val watchPageResponseData = viewModel.watchPageResponseDataFlow.collectAsState(initial = null)
    // 読み込み中？
    val isLoading = viewModel.isLoadingFlow.collectAsState(initial = true)

    if (!isLoading.value && watchPageResponseData.value != null) {

        // スリープにしない
        DisableSleepComposeApp()

        MiniPlayerCompose(
            backgroundContent = backScreen,
            playerContent = {
                // プレイヤー部分
                VideoPlayerUI(watchPageResponseData = watchPageResponseData.value!!)
            },
            detailContent = {
                // 動画情報
                VideoDetailUI(watchPageResponseData = watchPageResponseData.value!!)
            }
        )

    }

}