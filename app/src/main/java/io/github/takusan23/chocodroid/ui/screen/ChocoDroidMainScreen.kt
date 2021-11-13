package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.*
import io.github.takusan23.chocodroid.ui.theme.ChocoDroidTheme
import io.github.takusan23.chocodroid.ui.tool.SetActivitySleepComposeApp
import io.github.takusan23.chocodroid.ui.tool.SetNavigationBarColor
import io.github.takusan23.chocodroid.ui.tool.SetStatusBarColor
import io.github.takusan23.chocodroid.ui.tool.calcM3ElevationColor
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel
import kotlinx.coroutines.launch

/**
 * MainActivityに置くやつ。メイン画面です。
 *
 * Entry Point
 *
 * Jetpack Composeのスタート地点
 *
 * @param viewModel ViewModel
 * */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ChocoDroidMainScreen(viewModel: MainScreenViewModel) {
    ChocoDroidTheme {
        Surface {

            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            // 画面遷移。ナビゲーション
            val navController = rememberNavController()
            // BottomSheetの画面遷移
            val bottomSheetNavHostController = rememberNavController()
            // 再生中の動画情報
            val watchPageResponseData = viewModel.watchPageResponseDataFlow.collectAsState(initial = null)
            // コンテンツURL
            val mediaUrlData = viewModel.mediaUrlDataFlow.collectAsState(initial = null)
            // エラーが流れてくるFlow
            val errorData = viewModel.errorMessageFlow.collectAsState(initial = null)
            // BottomSheetの状態
            val modalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

            // 動画ミニプレイヤー
            val miniPlayerState = rememberMiniPlayerState(initialState = MiniPlayerStateValue.End) {
                // 終了したら
                if (it == MiniPlayerStateValue.End) {
                    viewModel.closePlayer()
                }
            }

            // スリープモード制御
            SetActivitySleepComposeApp(isEnable = watchPageResponseData.value != null)
            // ナビゲーションバーの色。BottomNavigationの色に合わせている。ボトムシート表示中は色を戻す
            SetNavigationBarColor(color = if (!modalBottomSheetState.isVisible) calcM3ElevationColor(
                colorScheme = MaterialTheme.colorScheme,
                color = MaterialTheme.colorScheme.surface,
                elevation = 3.dp
            ) else MaterialTheme.colorScheme.surface)
            // ステータスバーの色
            SetStatusBarColor(color = MaterialTheme.colorScheme.surface)

            // 動画情報更新したらミニプレイヤーの状態も変更
            LaunchedEffect(key1 = watchPageResponseData.value, block = {
                miniPlayerState.setState(if (watchPageResponseData.value != null) MiniPlayerStateValue.Default else MiniPlayerStateValue.End)
            })

            // Snackbar出す
            val scaffoldState = androidx.compose.material3.rememberScaffoldState()
            val snackbarHostState = remember { SnackbarHostState() }
            LaunchedEffect(key1 = errorData.value, block = {
                if (errorData.value != null) {
                    val result = snackbarHostState.showSnackbar(errorData.value!!, actionLabel = context.getString(R.string.close), duration = SnackbarDuration.Indefinite)
                    if (result == SnackbarResult.ActionPerformed) {
                        // 閉じるボタン押したら閉じる
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }
            })

            // MiniPlayerとScaffoldをつなげたもの
            MiniPlayerScaffold(
                scaffoldState = scaffoldState,
                miniPlayerState = miniPlayerState,
                snackbarHostState = snackbarHostState,
                modalBottomSheetState = modalBottomSheetState,
                bottomBar = { HomeScreenBottomNavigation(navHostController = navController) },
                playerContent = {
                    // 動画再生
                    if (watchPageResponseData.value != null && mediaUrlData.value != null) {
                        // ExoPlayerコントロール用
                        val exoPlayerComposeController = rememberExoPlayerComposeController(true)
                        VideoPlayerUI(
                            watchPageData = watchPageResponseData.value!!,
                            mediaUrlData = mediaUrlData.value!!,
                            controller = exoPlayerComposeController
                        )
                        VideoControlUI(
                            watchPageData = watchPageResponseData.value!!,
                            mediaUrlData = mediaUrlData.value!!,
                            controller = exoPlayerComposeController,
                            state = miniPlayerState,
                            onBottomSheetNavigate = { route ->
                                bottomSheetNavHostController.navigate(route)
                                scope.launch { modalBottomSheetState.show() }
                            }
                        )
                    }
                },
                detailContent = {
                    // 動画情報
                    watchPageResponseData.value?.apply {
                        VideoDetailScreen(
                            watchPageData = this,
                            miniPlayerState = miniPlayerState,
                            mainViewModel = viewModel,
                            mainNavHostController = navController,
                        )
                    }
                },
                bottomSheetContent = {
                    // ボトムシートの内容
                    ChocoDroidBottomSheetNavigation(
                        mainScreenViewModel = viewModel,
                        bottomSheetNavHostController = bottomSheetNavHostController,
                        modalBottomSheetState = modalBottomSheetState
                    )
                },
                content = {
                    // 画面遷移。別コンポーネントへ
                    ChocoDroidNavigation(
                        navController = navController,
                        mainScreenViewModel = viewModel,
                        onBottomSheetNavigate = { route ->
                            bottomSheetNavHostController.navigate(route)
                            scope.launch { modalBottomSheetState.show() }
                        }
                    )
                }
            )
        }
    }
}
