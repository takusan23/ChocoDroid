package io.github.takusan23.chocodroid.ui.screen

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.ui.component.SettingScreen
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.BottomSheetInitData
import io.github.takusan23.chocodroid.viewmodel.factory.ChannelScreenViewModelFactory
import io.github.takusan23.chocodroid.viewmodel.factory.ChocoBridgeSearchScreenViewModelFactory
import io.github.takusan23.chocodroid.viewmodel.factory.SearchScreenViewModelFactory

/**
 * ナビゲーション。画面遷移。
 *
 * [ChocoDroidMainScreen]のインテントというかネスト的にしんどくなってきたので切り出した
 *
 * @param navController 画面遷移コントローラー
 * @param onLoadWatchPage 動画を再生してほしいときに呼ばれます。動画IDが渡されます
 * @param onLoadWatchPageFromLocal [onLoadWatchPage]のダウンロードコンテンツ版
 * @param onBottomSheetNavigate BottomSheetの画面遷移と表示をしてほしいときに呼ばれる
 */
@Composable
fun ChocoDroidNavigation(
    navController: NavHostController = rememberNavController(),
    onLoadWatchPage: (String) -> Unit = {},
    onLoadWatchPageFromLocal: (String) -> Unit = {},
    onBottomSheetNavigate: (BottomSheetInitData) -> Unit = {},
) {
    val application = (LocalContext.current as ComponentActivity).application

    // 画面遷移
    NavHost(navController = navController, startDestination = NavigationLinkList.FavouriteScreen) {
        composable(NavigationLinkList.getSearchScreenLink("{query}")) {
            // 検索画面
            val searchQuery = it.arguments?.getString("query")!!
            SearchScreen(
                viewModel = viewModel(factory = SearchScreenViewModelFactory(application, searchQuery)),
                navController = navController,
                onClick = onLoadWatchPage,
                onBottomSheetNavigate = onBottomSheetNavigate
            )
        }
        composable(NavigationLinkList.FavouriteScreen) {
            // お気に入り画面
            FavouriteScreen(
                navController = navController,
                onLoadWatchPage = onLoadWatchPage,
                onBottomSheetNavigate = onBottomSheetNavigate
            )
        }
        composable(NavigationLinkList.HistoryScreen) {
            // 履歴画面
            HistoryScreen(
                navController = navController,
                onBottomSheetNavigate = onBottomSheetNavigate,
                onLoadWatchPage = onLoadWatchPage
            )
        }
        composable(NavigationLinkList.DownloadScreen) {
            // ダウンロード画面
            DownloadScreen(
                navController = navController,
                downloadScreenVideModel = viewModel(),
                onBottomSheetNavigate = onBottomSheetNavigate,
                onLoadWatchPageFromLocal = onLoadWatchPageFromLocal
            )
        }
        composable(NavigationLinkList.getChannelScreenLink("{channel_id}")) {
            // チャンネル画面
            val channelId = it.arguments?.getString("channel_id")!!
            ChannelScreen(
                channelScreenViewModel = viewModel(factory = ChannelScreenViewModelFactory(application, channelId)),
                onClick = onLoadWatchPage,
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavigationLinkList.getChocoDroidBridgeSearchScreen("{word}")) {
            // 検索入力画面
            val searchWord = it.arguments?.getString("word") ?: ""
            ChocoBridgeSearchScreen(
                bridgeSearchScreenViewModel = viewModel(factory = ChocoBridgeSearchScreenViewModelFactory(application, searchWord)),
                navController = navController,
                onLoadWatchPage = onLoadWatchPage
            )
        }
        composable(NavigationLinkList.SettingScreen) {
            // 設定画面
            SettingScreen()
        }
    }

}