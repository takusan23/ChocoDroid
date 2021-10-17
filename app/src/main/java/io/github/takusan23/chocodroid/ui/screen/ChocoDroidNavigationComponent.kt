package io.github.takusan23.chocodroid.ui.screen

import androidx.activity.ComponentActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel
import io.github.takusan23.chocodroid.viewmodel.SearchScreenViewModel
import io.github.takusan23.chocodroid.viewmodel.factory.SearchScreenViewModelFactory
import io.github.takusan23.htmlparse.html.SearchAPI

/**
 * ナビゲーション。画面遷移。
 *
 * [ChocoDroidMainScreen]のインテントというかネスト的にしんどくなってきたので切り出した
 *
 * @param navController 画面遷移コントローラー
 * @param mainScreenViewModel 最初の画面のViewModel
 * */
@ExperimentalMaterialApi
@Composable
fun ChocoDroidNavigationComponent(mainScreenViewModel: MainScreenViewModel, navController: NavHostController = rememberNavController()) {

    // 画面遷移
    NavHost(navController = navController, startDestination = NavigationLinkList.FavouriteScreen) {
        composable("${NavigationLinkList.SearchScreen}?query={query}&sort={sort}") {
            // 検索画面
            val searchQuery = it.arguments?.getString("query")!!
            val sort = it.arguments?.getString("sort") ?: SearchAPI.PARAMS_SORT_RELEVANCE
            val application = (LocalContext.current as ComponentActivity).application
            SearchScreen(
                viewModel = viewModel(factory = SearchScreenViewModelFactory(application, searchQuery, sort)),
                onBack = { navController.popBackStack() },
                onClick = { videoId -> mainScreenViewModel.loadWatchPage(videoId) }
            )
        }
        composable(NavigationLinkList.FavouriteScreen) {
            // お気に入り画面
            FavouriteScreen(viewModel = mainScreenViewModel, navController = navController)
        }
        composable(NavigationLinkList.HistoryScreen) {
            // 履歴画面
            HistoryScreen()
        }
        composable(NavigationLinkList.DownloadScreen) {
            // ダウンロード画面
            DownloadScreen()
        }
    }

}