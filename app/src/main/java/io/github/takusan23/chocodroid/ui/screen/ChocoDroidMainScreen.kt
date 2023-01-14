package io.github.takusan23.chocodroid.ui.screen

import android.view.SurfaceView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.player.PlayerState
import io.github.takusan23.chocodroid.service.SmoothChocoPlayerService
import io.github.takusan23.chocodroid.ui.component.*
import io.github.takusan23.chocodroid.ui.theme.ChocoDroidTheme
import io.github.takusan23.chocodroid.ui.theme.SurfaceElevations
import io.github.takusan23.chocodroid.ui.tool.PictureInPictureTool
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
 * @param pictureInPictureTool ピクチャーインピクチャー
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChocoDroidMainScreen(
    viewModel: MainScreenViewModel,
    pictureInPictureTool: PictureInPictureTool,
) {
    ChocoDroidTheme {
        Surface(color = MaterialTheme.colorScheme.background) {

            val context = LocalContext.current
            val lifecycle = LocalLifecycleOwner.current
            val scope = rememberCoroutineScope()
            // 画面遷移。ナビゲーション
            val navController = rememberNavController()
            // BottomSheetの画面遷移用Flow
            val bottomSheetInitData = viewModel.bottomSheetNavigation.collectAsStateWithLifecycle()
            // ピクチャーインピクチャーモードかどうか
            val isPictureInPictureMode = viewModel.pictureInPictureMode.collectAsStateWithLifecycle()

            // 動画プレイヤーサービスとバインドする
            val smoothChocoPlayerService = remember { SmoothChocoPlayerService.bindSmoothChocoPlayer(context, lifecycle) }.collectAsStateWithLifecycle(initialValue = null)
            // 動画プレイヤー
            val chocoDroidPlayer = smoothChocoPlayerService.value?.chocoDroidPlayer
            // 再生中の動画情報
            val watchPageResponseData = smoothChocoPlayerService.value?.watchPageResponseDataFlow?.collectAsStateWithLifecycle(initialValue = null)
            // エラーが流れてくるFlow
            val errorData = smoothChocoPlayerService.value?.errorMessageFlow?.collectAsStateWithLifecycle(initialValue = null)
            // 画質
            val currentQuality = smoothChocoPlayerService.value?.currentQualityData?.collectAsStateWithLifecycle(initialValue = null)
            // プレイヤー状態
            val playbackState = smoothChocoPlayerService.value?.playbackStateFlow?.collectAsStateWithLifecycle()
            // 動画ファイルの情報
            val videoMetaData = smoothChocoPlayerService.value?.videoMetaDataFlow?.collectAsStateWithLifecycle()
            // 再生位置
            val currentPositionData = smoothChocoPlayerService.value?.currentPositionDataFlow?.collectAsStateWithLifecycle()

            // BottomSheetの状態
            val modalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
            // 動画ミニプレイヤー
            val miniPlayerState = rememberMiniPlayerState(initialState = MiniPlayerStateType.EndOrHide) {
                // 終了したら
                if (it == MiniPlayerStateType.EndOrHide) {
                    smoothChocoPlayerService.value?.destroy()
                }
            }

            // BottomSheetをバックキーで閉じれるように。isVisibleのときのみバックキーを監視
            BackHandler(modalBottomSheetState.isVisible) {
                scope.launch { modalBottomSheetState.hide() }
            }

            // スリープモード制御
            SetActivitySleepComposeApp(isEnable = watchPageResponseData?.value != null)

            // ナビゲーションバーの色
            // BottomNavigationの色に合わせている。ボトムシート表示中とか標準プレイヤー時の色
            SetNavigationBarColor(
                color = when {
                    modalBottomSheetState.isVisible -> MaterialTheme.colorScheme.background
                    miniPlayerState.currentState.value == MiniPlayerStateType.Default -> MaterialTheme.colorScheme.surfaceColorAtElevation(SurfaceElevations.VideoDetailBackgroundElevation)
                    else -> MaterialTheme.colorScheme.surfaceColorAtElevation(3.0.dp)
                }
            )
            // ステータスバーの色
            SetStatusBarColor(color = MaterialTheme.colorScheme.background)

            // 動画情報更新したらミニプレイヤーの状態も変更
            LaunchedEffect(key1 = watchPageResponseData?.value) {
                miniPlayerState.setState(if (watchPageResponseData?.value != null) MiniPlayerStateType.Default else MiniPlayerStateType.EndOrHide)
            }

            // Snackbar出す
            val snackbarHostState = remember { SnackbarHostState() }
            LaunchedEffect(key1 = errorData?.value) {
                if (errorData?.value != null) {
                    val result = snackbarHostState.showSnackbar(errorData.value!!, actionLabel = context.getString(R.string.close), duration = SnackbarDuration.Indefinite)
                    if (result == SnackbarResult.ActionPerformed) {
                        // 閉じるボタン押したら閉じる
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }
            }

            // ピクチャーインピクチャーが再生中のみ有効になるように
            LaunchedEffect(key1 = playbackState?.value) {
                pictureInPictureTool.isEnablePictureInPicture = smoothChocoPlayerService.value?.isContentPlaying == true
            }

            // MiniPlayerとScaffoldをつなげたもの
            MiniPlayerScaffold(
                miniPlayerState = miniPlayerState,
                snackbarHostState = snackbarHostState,
                modalBottomSheetState = modalBottomSheetState,
                bottomBar = { HomeScreenBottomNavigation(navHostController = navController) },
                playerContent = {
                    // 動画再生
                    if (
                        watchPageResponseData?.value != null
                        && playbackState?.value != null
                        && currentPositionData?.value != null
                        && currentQuality?.value != null
                        && videoMetaData?.value != null
                    ) {
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

                        // SurfaceViewのセット と ピクチャーインピクチャーの有効化
                        val surfaceView = remember { SurfaceView(context) }
                        LaunchedEffect(key1 = playbackState.value) {
                            when (playbackState.value) {
                                PlayerState.Buffering,
                                PlayerState.Pause,
                                PlayerState.Play,
                                -> chocoDroidPlayer?.setSurfaceView(surfaceView)
                                else -> {
                                    // do nothing
                                }
                            }
                        }
                        DisposableEffect(key1 = Unit) { onDispose { chocoDroidPlayer?.clearSurface() } }

                        // SurfaceViewとコントローラーセット
                        ExoPlayerComposeUI(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .onGloballyPositioned {
                                    // 座標変化したらPinPのパラメーターも更新するため ViewModel に渡す
                                    viewModel.setPictureInPictureRect(
                                        it
                                            .boundsInWindow()
                                            .toAndroidRect()
                                    )
                                },
                            videoMetaData = videoMetaData.value,
                            playbackState = playbackState.value,
                            surfaceView = surfaceView
                        )
                        // ピクチャーインピクチャー時は表示しないので
                        if (chocoDroidPlayer != null) {
                            VideoControlUI(
                                watchPageData = watchPageResponseData.value!!,
                                mediaUrlData = currentQuality.value!!,
                                chocoDroidPlayer = chocoDroidPlayer,
                                videoMetaData = videoMetaData.value,
                                currentPositionData = currentPositionData.value,
                                miniPlayerState = miniPlayerState,
                                onPictureInPictureClick = { pictureInPictureTool.enterPictureInPicture() },
                                onBottomSheetNavigate = { route ->
                                    viewModel.navigateBottomSheet(route)
                                    scope.launch { modalBottomSheetState.show() }
                                }
                            )
                        }
                    }
                },
                detailContent = {
                    // 動画情報
                    watchPageResponseData?.value?.also { watchPageData ->
                        VideoDetailScreen(
                            watchPageData = watchPageData,
                            miniPlayerState = miniPlayerState,
                            mainNavHostController = navController,
                            onLoadWatchPage = { videoId -> smoothChocoPlayerService.value?.loadWatchPage(videoId) }
                        ) {
                            viewModel.navigateBottomSheet(it)
                            scope.launch { modalBottomSheetState.show() }
                        }
                    }
                },
                bottomSheetContent = {
                    // ボトムシートの内容
                    if (smoothChocoPlayerService.value != null) {
                        ChocoDroidBottomSheetNavigation(
                            smoothChocoPlayerService = smoothChocoPlayerService.value!!,
                            bottomSheetInitData = bottomSheetInitData.value,
                            modalBottomSheetState = modalBottomSheetState,
                            onBottomSheetNavigate = {
                                viewModel.navigateBottomSheet(it)
                                scope.launch { modalBottomSheetState.show() }
                            }
                        )
                    } else {
                        // The initial value must have an associated anchor. 対策。何もない状態だとエラーが出るので適当においておく
                        Box(modifier = Modifier.fillMaxSize())
                    }
                }
            ) {
                // 画面遷移。別コンポーネントへ
                ChocoDroidNavigation(
                    navController = navController,
                    onLoadWatchPage = { videoId -> smoothChocoPlayerService.value?.loadWatchPage(videoId) },
                    onLoadWatchPageFromLocal = { videoId -> smoothChocoPlayerService.value?.loadWatchPageFromLocal(videoId) },
                    onBottomSheetNavigate = { route ->
                        viewModel.navigateBottomSheet(route)
                        scope.launch { modalBottomSheetState.show() }
                    }
                )
            }
        }
    }
}
