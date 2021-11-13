package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.screen.favourite.FavoriteFolderListScreen
import io.github.takusan23.chocodroid.ui.screen.favourite.FavoriteVideoListScreen
import io.github.takusan23.chocodroid.ui.screen.favourite.FavouriteScreenNavigationLinkList
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * お気に入り画面
 *
 * @param viewModel メイン画面のViewModel
 * @param navController メイン画面のNavController
 * @param onBottomSheetNavigate BottomSheetの表示と画面遷移してほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    viewModel: MainScreenViewModel,
    navController: NavHostController,
    onBottomSheetNavigate: (String) -> Unit,
) {
    // 画面遷移
    val favouriteNavController = rememberNavController()

    M3Scaffold(
        topBar = { ChocoBridgeBar(viewModel = viewModel, navHostController = navController) },
        content = {
            Column(modifier = Modifier.padding(it)) {
                // ルーティング
                NavHost(navController = favouriteNavController, startDestination = FavouriteScreenNavigationLinkList.FolderList) {
                    composable(FavouriteScreenNavigationLinkList.FolderList) {
                        // フォルダ一覧
                        FavoriteFolderListScreen(
                            onVideoListNavigate = { folderId -> favouriteNavController.navigate(FavouriteScreenNavigationLinkList.getFolderVideoList(folderId.toString())) },
                            onFabClick = { route -> onBottomSheetNavigate(route) }
                        )
                    }
                    composable(FavouriteScreenNavigationLinkList.getFolderVideoList("{folder_id}")) { backStackEntry ->
                        val folderId = backStackEntry.arguments?.getString("folder_id")!!
                        // フォルダの中身
                        FavoriteVideoListScreen(
                            mainScreenViewModel = viewModel,
                            folderId = folderId,
                            onBack = { favouriteNavController.popBackStack() }
                        )
                    }
                }
            }
        }
    )
}