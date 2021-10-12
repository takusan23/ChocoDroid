package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.takusan23.chocodroid.ui.component.*
import io.github.takusan23.chocodroid.ui.component.tool.SetActivitySleepComposeApp
import io.github.takusan23.chocodroid.ui.theme.ChocoDroidTheme
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

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

            // 再生中の動画情報
            val watchPageResponseData = viewModel.watchPageResponseDataFlow.collectAsState(initial = null)
            // スリープモード制御
            SetActivitySleepComposeApp(isEnable = watchPageResponseData.value != null)

            // 動画ミニプレイヤー
            val miniPlayerState = rememberMiniPlayerState {
                // 終了 or コンポーネントが終了 したら
                if (it == MiniPlayerStateValue.End || it == MiniPlayerStateValue.Destroy) {
                    viewModel.closePlayer()
                }
            }

            // MiniPlayerとScaffoldをつなげたもの
            MiniPlayerScaffold(
                miniPlayerState = miniPlayerState,
                bottomBar = { HomeScreenBottomNavigation() },
                isShowMiniPlayer = watchPageResponseData.value != null,
                playerContent = {
                    // 動画再生
                    watchPageResponseData.value?.apply {
                        val exoPlayerComposeController = rememberExoPlayerComposeController(true)
                        VideoPlayerUI(watchPageResponseData = this, exoPlayerComposeController)
                        VideoControlUI(watchPageResponseData = this, controller = exoPlayerComposeController, miniPlayerState = miniPlayerState)
                    }
                },
                detailContent = {
                    // 動画情報
                    watchPageResponseData.value?.apply { VideoDetailUI(this) }
                },
                content = {
                    // 将来的に
                    Scaffold(
                        topBar = { HomeScreenSearchBox(onEnterVideoId = { viewModel.loadWatchPage(it) }) },
                        content = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "工事中")
                            }
                        }
                    )
                }
            )

        }
    }
}
