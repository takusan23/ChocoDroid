package io.github.takusan23.chocodroid.ui.screen

import android.view.SurfaceView
import androidx.activity.compose.BackHandler
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.ChocoDroidApplication
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.player.PlayerState
import io.github.takusan23.chocodroid.ui.component.*
import io.github.takusan23.chocodroid.ui.theme.ChocoDroidTheme
import io.github.takusan23.chocodroid.ui.theme.SurfaceElevations
import io.github.takusan23.chocodroid.ui.tool.SetActivitySleepComposeApp
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
 * @param onPictureInPictureModeChange ピクチャーインピクチャーボタンが押されたら呼び出す
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun ChocoDroidMainScreen(
    viewModel: MainScreenViewModel,
    onPictureInPictureModeChange: () -> Unit = {},
) {
    ChocoDroidTheme {
        Surface(color = MaterialTheme.colorScheme.background) {

            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            // 動画プレイヤー
            val chocoDroidPlayer = remember { ChocoDroidApplication.instance.chocoDroidPlayer }
            // 動画ローダー
            val contentLoader = remember { ChocoDroidApplication.instance.chocoDroidContentLoader }
            // 画面遷移。ナビゲーション
            val navController = rememberNavController()
            // BottomSheetの画面遷移用Flow
            val bottomSheetInitData = viewModel.bottomSheetNavigation.collectAsStateWithLifecycle()
            // ピクチャーインピクチャーモードかどうか
            val isPictureInPictureMode = viewModel.pictureInPictureMode.collectAsStateWithLifecycle()

            // 再生中の動画情報
            val watchPageResponseData = contentLoader.watchPageResponseDataFlow.collectAsStateWithLifecycle(initialValue = null)
            // コンテンツURL
            val mediaUrlData = contentLoader.mediaUrlData.collectAsStateWithLifecycle(initialValue = null)
            // エラーが流れてくるFlow
            val errorData = contentLoader.errorMessageFlow.collectAsStateWithLifecycle(initialValue = null)
            // 動画情報
            val videoData = chocoDroidPlayer.videoDataFlow.collectAsStateWithLifecycle()
            // プレイヤー状態
            val playbackState = chocoDroidPlayer.playbackStateFlow.collectAsStateWithLifecycle()
            // 再生位置
            val currentPositionData = chocoDroidPlayer.currentPositionDataFlow.collectAsStateWithLifecycle()

            // BottomSheetの状態
            val modalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
            // 動画ミニプレイヤー
            val miniPlayerState = rememberMiniPlayerState(initialState = MiniPlayerStateType.EndOrHide) {
                // 終了したら
                if (it == MiniPlayerStateType.EndOrHide) {
                    ChocoDroidApplication.instance.playerDestroy()
                }
            }

            // BottomSheetをバックキーで閉じれるように。isVisibleのときのみバックキーを監視
            BackHandler(modalBottomSheetState.isVisible) {
                scope.launch { modalBottomSheetState.hide() }
            }

            // スリープモード制御
            SetActivitySleepComposeApp(isEnable = watchPageResponseData.value != null)

            // ナビゲーションバーの色
            // BottomNavigationの色に合わせている。ボトムシート表示中とか標準プレイヤー時の色
            SetNavigationBarColor(color = when {
                modalBottomSheetState.isVisible -> MaterialTheme.colorScheme.background
                miniPlayerState.currentState.value == MiniPlayerStateType.Default -> MaterialTheme.colorScheme.surfaceColorAtElevation(SurfaceElevations.VideoDetailBackgroundElevation)
                else -> MaterialTheme.colorScheme.surfaceColorAtElevation(3.0.dp)
            })
            // ステータスバーの色
            SetStatusBarColor(color = MaterialTheme.colorScheme.background)

            // 動画情報更新したらミニプレイヤーの状態も変更
            LaunchedEffect(key1 = watchPageResponseData.value) {
                miniPlayerState.setState(if (watchPageResponseData.value != null) MiniPlayerStateType.Default else MiniPlayerStateType.EndOrHide)
            }

            // Snackbar出す
            val snackbarHostState = remember { SnackbarHostState() }
            LaunchedEffect(key1 = errorData.value) {
                if (errorData.value != null) {
                    val result = snackbarHostState.showSnackbar(errorData.value!!, actionLabel = context.getString(R.string.close), duration = SnackbarDuration.Indefinite)
                    if (result == SnackbarResult.ActionPerformed) {
                        // 閉じるボタン押したら閉じる
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }
            }

            // MiniPlayerとScaffoldをつなげたもの
            MiniPlayerScaffold(
                miniPlayerState = miniPlayerState,
                snackbarHostState = snackbarHostState,
                modalBottomSheetState = modalBottomSheetState,
                bottomBar = { HomeScreenBottomNavigation(navHostController = navController) },
                playerContent = {
                    // 動画再生
                    if (watchPageResponseData.value != null && mediaUrlData.value != null) {

                        // 通常表示のときのみバックキーを監視して、バックキーでミニプレイヤーに遷移できるようにする
                        // ここに書くと視聴画面が表示されたときに登録される
                        BackHandler(miniPlayerState.currentState.value == MiniPlayerStateType.Default || miniPlayerState.currentState.value == MiniPlayerStateType.Fullscreen) {
                            when (miniPlayerState.currentState.value) {
                                MiniPlayerStateType.Default -> miniPlayerState.setState(MiniPlayerStateType.MiniPlayer)
                                MiniPlayerStateType.Fullscreen -> miniPlayerState.setState(MiniPlayerStateType.Default)
                                else -> {
                                    // do nothing
                                }
                            }
                        }

                        // SurfaceView
                        val surfaceView = remember { SurfaceView(context) }
                        LaunchedEffect(key1 = playbackState.value) {
                            when (playbackState.value) {
                                PlayerState.Buffering,
                                PlayerState.Pause,
                                PlayerState.Play,
                                -> chocoDroidPlayer.setSurfaceView(surfaceView)
                                else -> {
                                    // do nothing
                                }
                            }
                        }
                        DisposableEffect(key1 = Unit) { onDispose { chocoDroidPlayer.clearSurface() } }

                        // SurfaceViewとコントローラーセット
                        ExoPlayerComposeUI(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .onGloballyPositioned {
                                    // 座標変化したらPinPのパラメーターも更新するため ViewModel に渡す
                                    viewModel.setPictureInPictureRect(it
                                        .boundsInWindow()
                                        .toAndroidRect())
                                },
                            videoData = videoData.value,
                            playbackState = playbackState.value,
                            surfaceView = surfaceView
                        )
                        // ピクチャーインピクチャー時は表示しないので
                        VideoControlUI(
                            watchPageData = watchPageResponseData.value!!,
                            mediaUrlData = mediaUrlData.value!!,
                            chocoDroidPlayer = chocoDroidPlayer,
                            videoData = videoData.value,
                            currentPositionData = currentPositionData.value,
                            miniPlayerState = miniPlayerState,
                            onPictureInPictureClick = onPictureInPictureModeChange,
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
                            mainNavHostController = navController,
                            onLoadWatchPage = { contentLoader.loadWatchPage(it) }
                        ) {
                            viewModel.navigateBottomSheet(it)
                            scope.launch { modalBottomSheetState.show() }
                        }
                    }
                },
                bottomSheetContent = {
                    // ボトムシートの内容
                    ChocoDroidBottomSheetNavigation(
                        bottomSheetInitData = bottomSheetInitData.value,
                        modalBottomSheetState = modalBottomSheetState,
                        chocoDroidContentLoader = contentLoader,
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
                        onLoadWatchPage = { videoId -> contentLoader.loadWatchPage(videoId) },
                        onLoadWatchPageFromLocal = { videoId -> contentLoader.loadWatchPageFromLocal(videoId) }
                    ) { route ->
                        viewModel.navigateBottomSheet(route)
                        scope.launch { modalBottomSheetState.show() }
                    }
                }
            )
        }
    }
}
