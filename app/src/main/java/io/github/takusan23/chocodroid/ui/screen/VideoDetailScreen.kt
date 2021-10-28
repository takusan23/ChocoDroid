package io.github.takusan23.chocodroid.ui.component

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.service.ContentDownloadService
import io.github.takusan23.chocodroid.ui.component.tool.calcM3ElevationColor
import io.github.takusan23.chocodroid.ui.screen.videodetail.*
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel
import io.github.takusan23.internet.data.watchpage.WatchPageData

/**
 * 動画説明部分のUI
 *
 * @param watchPageData 視聴ページレスポンスデータ
 * @param mainViewModel メイン画面ViewModel
 * @param miniPlayerState ミニプレイヤー操作用
 * @param navHostController 動画詳細とかメニュー切り替え
 * @param mainNavHostController メイン画面のNavController
 * */
@Composable
fun VideoDetailScreen(
    watchPageData: WatchPageData,
    mainViewModel: MainScreenViewModel,
    miniPlayerState: MiniPlayerState = rememberMiniPlayerState(),
    navHostController: NavHostController = rememberNavController(),
    mainNavHostController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 戻るキーでミニプレイヤーにできるように
    SetPressBackKeyToMiniPlayer(miniPlayerState = miniPlayerState, navHostController = navHostController)
    // BottomNavの色を出す
    val bottomNavColor = calcM3ElevationColor(
        colorScheme = MaterialTheme.colorScheme,
        color = MaterialTheme.colorScheme.surface,
        elevation = 3.dp
    )

    Surface(
        color = bottomNavColor,
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            // NavigationRail
            VideoDetailNavigationRail(
                navHostController = navHostController,
                watchPageData = watchPageData
            )

            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
            ) {
                NavHost(navController = navHostController, startDestination = VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) {
                    // 動画説明
                    composable(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) {
                        VideoDetailDescriptionScreen(
                            watchPageData = watchPageData,
                            onNavigation = {
                                miniPlayerState.setState(MiniPlayerStateValue.MiniPlayer)
                                mainNavHostController.navigate(it)
                            }
                        )
                    }
                    // メニュー画面
                    composable(VideoDetailNavigationLinkList.VideoDetailMenuScreen) {
                        VideoDetailMenuScreen(watchPageData = watchPageData)
                    }
                    // 関連動画
                    composable(VideoDetailNavigationLinkList.VideoDetailRelatedVideos) {
                        VideoDetailRelatedVideoScreen(
                            watchPageData = watchPageData,
                            onClick = { mainViewModel.loadWatchPage(it) }
                        )
                    }
                    // ダウンロード
                    composable(VideoDetailNavigationLinkList.VideoDetailDownloadScreen) {
                        VideoDetailDownloadScreen(
                            watchPageData = watchPageData,
                            onDownloadClick = { data -> ContentDownloadService.startDownloadService(context, data) },
                            onDeleteClick = { mainViewModel.deleteDownloadContent(it) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * バックキーでミニプレイヤーへ遷移する関数
 *
 * 動画説明文[VideoDetailNavigationLinkList.VideoDetailDescriptionScreen]で、プレイヤーの状態が[MiniPlayerStateValue.Default]のときは
 *
 * バックキーで遷移できるようにします。それ以外の場合はバックキーのコールバックを無効にします。
 *
 * 多分ContextがActivityじゃないと動かない
 *
 * @param miniPlayerState ミニプレイヤー操作用
 * @param navHostController 今の画面を取得するのに使う
 * */
@Composable
private fun SetPressBackKeyToMiniPlayer(
    miniPlayerState: MiniPlayerState?,
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val currentNavEntry = navHostController.currentBackStackEntryAsState()

    /** 戻るキーコールバック */
    val backCallback = remember {
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                miniPlayerState?.setState(MiniPlayerStateValue.MiniPlayer)
            }
        }
    }
    // 戻るキーコールバックを有効にするか。動画説明画面でミニプレイヤーじゃない場合は有効にする
    backCallback.isEnabled = currentNavEntry.value?.destination?.route == VideoDetailNavigationLinkList.VideoDetailDescriptionScreen && miniPlayerState?.currentState?.value == MiniPlayerStateValue.Default
    // 戻るキーコールバックを登録して、Composeが破棄されたら解除する
    DisposableEffect(key1 = Unit, effect = {
        if (context is ComponentActivity) {
            context.onBackPressedDispatcher.addCallback(backCallback)
        }
        onDispose {
            backCallback.remove()
        }
    })

}