package io.github.takusan23.chocodroid.ui.screen

import android.view.SurfaceView
import androidx.activity.compose.BackHandler
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.ChocoDroidApplication
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.*
import io.github.takusan23.chocodroid.ui.theme.ChocoDroidTheme
import io.github.takusan23.chocodroid.ui.theme.SurfaceElevations
import io.github.takusan23.chocodroid.ui.tool.SetActivitySleepComposeApp
import io.github.takusan23.chocodroid.ui.tool.SetNavigationBarColor
import io.github.takusan23.chocodroid.ui.tool.SetStatusBarColor
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel
import io.github.takusan23.internet.data.watchpage.MediaUrlData
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
@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun ChocoDroidMainScreen(viewModel: MainScreenViewModel) {
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
            val bottomSheetInitData = viewModel.bottomSheetNavigation.collectAsState()

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

            // 通常表示のときのみバックキーを監視して、バックキーでミニプレイヤーに遷移できるようにする
            BackHandler(miniPlayerState.currentState.value == MiniPlayerStateType.Default) {
                miniPlayerState.setState(MiniPlayerStateType.MiniPlayer)
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
                        // SurfaceView
                        val surfaceView = remember { SurfaceView(context) }
                        // プレイヤー作成
                        DisposableEffect(key1 = Unit) {
                            chocoDroidPlayer.createPlayer()
                            chocoDroidPlayer.setSurfaceView(surfaceView)
                            onDispose {
                                chocoDroidPlayer.clearSurface()
                            }
                        }

                        LaunchedEffect(key1 = mediaUrlData.value) {
                            mediaUrlData.value!!.apply {
                                // Hls/DashのManifestがあればそれを読み込む（生放送、一部の動画）。
                                // ない場合は映像、音声トラックをそれぞれ渡す
                                if (mixTrackUrl != null) {
                                    val isDash = urlType == MediaUrlData.MediaUrlType.TYPE_DASH
                                    chocoDroidPlayer.setMediaSourceUri(mixTrackUrl!!, isDash)
                                } else {
                                    // 動画URLを読み込む
                                    chocoDroidPlayer.setMediaSourceVideoAudioUriSupportVer(videoTrackUrl!!, audioTrackUrl!!)
                                }
                            }
                            // ダブルタップシークを実装した際に、初回ロード中にダブルタップすることで即時再生されることを発見したので、
                            // わからないレベルで進めておく。これで初回のめっちゃ長い読み込みが解決する？
                            if (watchPageResponseData.value?.isLiveContent == false) {
                                chocoDroidPlayer.currentPositionMs = 10L
                            }
                        }

                        // SurfaceViewとコントローラーセット
                        ExoPlayerComposeUI(
                            videoData = videoData.value,
                            playbackState = playbackState.value,
                            surfaceView = surfaceView
                        )
                        VideoControlUI(
                            watchPageData = watchPageResponseData.value!!,
                            mediaUrlData = mediaUrlData.value!!,
                            chocoDroidPlayer = chocoDroidPlayer,
                            videoData = videoData.value,
                            currentPositionData = currentPositionData.value,
                            miniPlayerState = miniPlayerState,
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
