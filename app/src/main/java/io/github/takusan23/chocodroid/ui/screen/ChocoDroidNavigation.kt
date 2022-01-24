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
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel
import io.github.takusan23.chocodroid.viewmodel.factory.ChannelScreenViewModelFactory
import io.github.takusan23.chocodroid.viewmodel.factory.SearchScreenViewModelFactory

/**
 * ナビゲーション。画面遷移。
 *
 * [ChocoDroidMainScreen]のインテントというかネスト的にしんどくなってきたので切り出した
 *
 * @param navController 画面遷移コントローラー
 * @param mainScreenViewModel 最初の画面のViewModel
 * @param onBottomSheetNavigate BottomSheetの画面遷移と表示をしてほしいときに呼ばれる
 * */
@Composable
fun ChocoDroidNavigation(
    mainScreenViewModel: MainScreenViewModel,
    navController: NavHostController = rememberNavController(),
    onBottomSheetNavigate: (BottomSheetInitData) -> Unit = {},
) {

    // 画面遷移
    NavHost(navController = navController, startDestination = NavigationLinkList.FavouriteScreen) {
        composable(NavigationLinkList.getSearchScreenLink("{query}")) {
            // 検索画面
            val searchQuery = it.arguments?.getString("query")!!
            val application = (LocalContext.current as ComponentActivity).application
            SearchScreen(
                viewModel = viewModel(factory = SearchScreenViewModelFactory(application, searchQuery)),
                onBack = { navController.popBackStack() },
                onClick = { videoId -> mainScreenViewModel.loadWatchPage(videoId) },
                onBottomSheetNavigate = onBottomSheetNavigate
            )
        }
        composable(NavigationLinkList.FavouriteScreen) {
            // お気に入り画面
            FavouriteScreen(
                viewModel = mainScreenViewModel,
                navController = navController,
                onBottomSheetNavigate = onBottomSheetNavigate
            )
        }
        composable(NavigationLinkList.HistoryScreen) {
            // 履歴画面
            HistoryScreen(
                mainViewModel = mainScreenViewModel,
                navController = navController,
                onBottomSheetNavigate = onBottomSheetNavigate
            )
        }
        composable(NavigationLinkList.DownloadScreen) {
            // ダウンロード画面
            DownloadScreen(
                mainScreenViewModel = mainScreenViewModel,
                navController = navController,
                downloadScreenVideModel = viewModel(),
                onBottomSheetNavigate = onBottomSheetNavigate
            )
        }
        composable(NavigationLinkList.getChannelScreenLink("{channel_id}")) {
            // チャンネル画面
            val channelId = it.arguments?.getString("channel_id")!!
            val application = (LocalContext.current as ComponentActivity).application
            ChannelScreen(
                channelScreenViewModel = viewModel(factory = ChannelScreenViewModelFactory(application, channelId)),
                onClick = { videoId -> mainScreenViewModel.loadWatchPage(videoId) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavigationLinkList.SettingScreen) {
            // 設定画面
            SettingScreen()
        }
    }

}