package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
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
@ExperimentalMaterialApi
@Composable
fun ChocoDroidMainScreen(viewModel: MainScreenViewModel) {
    ChocoDroidTheme {
        Surface {

            // 画面遷移。ナビゲーション
            val navController = rememberNavController()
            // 再生中の動画情報
            val watchPageResponseData = viewModel.watchPageResponseDataFlow.collectAsState(initial = null)
            // エラーが流れてくるFlow
            val errorData = viewModel.errorMessageFlow.collectAsState(initial = null)

            // 動画ミニプレイヤー
            val miniPlayerState = rememberMiniPlayerState(initialState = MiniPlayerStateValue.End) {
                // 終了したら
                if (it == MiniPlayerStateValue.End) {
                    viewModel.closePlayer()
                }
            }

            // スリープモード制御
            SetActivitySleepComposeApp(isEnable = watchPageResponseData.value != null)
            // 動画情報更新したらミニプレイヤーの状態も変更
            LaunchedEffect(key1 = watchPageResponseData.value, block = {
                miniPlayerState.currentState.value = if (watchPageResponseData.value != null) MiniPlayerStateValue.Default else MiniPlayerStateValue.End
            })

            // Snackbar出す
            val scaffoldState = rememberScaffoldState()
            LaunchedEffect(key1 = errorData.value, block = {
                if (errorData.value != null) {
                    val snackbarHostState = scaffoldState.snackbarHostState
                    val result = snackbarHostState.showSnackbar(errorData.value!!, actionLabel = "閉じる", duration = SnackbarDuration.Indefinite)
                    if (result == SnackbarResult.ActionPerformed) {
                        // 閉じるボタン押したら閉じる
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }
            })

            // MiniPlayerとScaffoldをつなげたもの
            MiniPlayerScaffold(
                miniPlayerState = miniPlayerState,
                scaffoldState = scaffoldState,
                bottomBar = { HomeScreenBottomNavigation() },
                playerContent = {
                    // 動画再生
                    watchPageResponseData.value?.apply {
                        val exoPlayerComposeController = rememberExoPlayerComposeController(true)
                        VideoPlayerUI(watchPageData = this, controller = exoPlayerComposeController)
                        VideoControlUI(watchPageData = this, controller = exoPlayerComposeController, state = miniPlayerState)
                    }
                },
                detailContent = {
                    // 動画情報
                    watchPageResponseData.value?.apply { VideoDetailUI(this) }
                },
                content = {
                    // 画面遷移。別コンポーネントへ
                    ChocoDroidNavigationComponent(navController = navController, mainScreenViewModel = viewModel)
                }
            )
        }
    }
}
