package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.*
import io.github.takusan23.chocodroid.ui.theme.ChocoDroidTheme
import io.github.takusan23.chocodroid.ui.tool.SetActivitySleepComposeApp
import io.github.takusan23.chocodroid.ui.tool.SetBackKeyEvent
import io.github.takusan23.chocodroid.ui.tool.SetNavigationBarColor
import io.github.takusan23.chocodroid.ui.tool.SetStatusBarColor
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
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChocoDroidMainScreen(viewModel: MainScreenViewModel) {
    ChocoDroidTheme {
        Surface(color = MaterialTheme.colorScheme.background) {

            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            // 画面遷移。ナビゲーション
            val navController = rememberNavController()
            // BottomSheetの画面遷移用Flow
            val bottomSheetInitData = viewModel.bottomSheetNavigation.collectAsState()
            // 再生中の動画情報
            val watchPageResponseData = viewModel.watchPageResponseDataFlow.collectAsState(initial = null)
            // コンテンツURL
            val mediaUrlData = viewModel.mediaUrlData.collectAsState(initial = null)
            // エラーが流れてくるFlow
            val errorData = viewModel.errorMessageFlow.collectAsState(initial = null)
            // BottomSheetの状態
            val modalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
            // 動画ミニプレイヤー
            val miniPlayerState = rememberMiniPlayerState(initialState = MiniPlayerStateType.EndOrHide) {
                // 終了したら
                if (it == MiniPlayerStateType.EndOrHide) {
                    viewModel.closePlayer()
                }
            }

            // BottomSheetをバックキーで閉じれるように。isVisibleのときのみバックキーを監視
            if (modalBottomSheetState.isVisible) {
                SetBackKeyEvent { scope.launch { modalBottomSheetState.hide() } }
            }

            // 通常表示のときのみバックキーを監視して、バックキーでミニプレイヤーに遷移できるようにする
            if (miniPlayerState.currentState.value == MiniPlayerStateType.Default) {
                SetBackKeyEvent { miniPlayerState.setState(MiniPlayerStateType.MiniPlayer) }
            }

            // スリープモード制御
            SetActivitySleepComposeApp(isEnable = watchPageResponseData.value != null)

            // ナビゲーションバーの色
            // BottomNavigationの色に合わせている。ボトムシート表示中とか標準プレイヤー時の色
            SetNavigationBarColor(color = when {
                modalBottomSheetState.isVisible -> MaterialTheme.colorScheme.background
                miniPlayerState.currentState.value == MiniPlayerStateType.Default -> MaterialTheme.colorScheme.inverseOnSurface
                else -> MaterialTheme.colorScheme.surfaceColorAtElevation(LocalAbsoluteTonalElevation.current + 3.0.dp)
            })
            // ステータスバーの色
            SetStatusBarColor(color = MaterialTheme.colorScheme.background)

            // 動画情報更新したらミニプレイヤーの状態も変更
            LaunchedEffect(key1 = watchPageResponseData.value, block = {
                miniPlayerState.setState(if (watchPageResponseData.value != null) {
                    MiniPlayerStateType.Default
                } else MiniPlayerStateType.EndOrHide)
            })

            // Snackbar出す
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
                            miniPlayerState = miniPlayerState,
                            state = miniPlayerState,
                            onBottomSheetNavigate = { route ->
                                viewModel.navigateBottomSheet(route)
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
                            onBottomSheetNavigate = {
                                viewModel.navigateBottomSheet(it)
                                scope.launch { modalBottomSheetState.show() }
                            }
                        )
                    }
                },
                bottomSheetContent = {
                    // ボトムシートの内容
                    ChocoDroidBottomSheetNavigation(
                        mainScreenViewModel = viewModel,
                        bottomSheetInitData = bottomSheetInitData.value,
                        modalBottomSheetState = modalBottomSheetState,
                        onBottomSheetNavigate = {
                            viewModel.navigateBottomSheet(it)
                            scope.launch { modalBottomSheetState.show() }
                        }
                    )
                },
                content = {
                    // 画面遷移。別コンポーネントへ
                    ChocoDroidNavigation(
                        navController = navController,
                        mainScreenViewModel = viewModel,
                        onBottomSheetNavigate = { route ->
                            viewModel.navigateBottomSheet(route)
                            scope.launch { modalBottomSheetState.show() }
                        }
                    )
                }
            )
        }
    }
}
