package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.ui.component.FavouriteTab
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.screen.favourite.FavouriteScreenNavigationLinkList
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * お気に入り画面
 *
 * @param viewModel メイン画面のViewModel
 * @param navController メイン画面のNavController
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(viewModel: MainScreenViewModel, navController: NavHostController) {
    // 画面遷移
    val favouriteNavController = rememberNavController()

    M3Scaffold(
        topBar = { ChocoBridgeBar(viewModel = viewModel, navHostController = navController) },
        content = {
            Column(modifier = Modifier.padding(it)) {
                // タブ
                FavouriteTab(navHostController = favouriteNavController)

                // ルーティング
                NavHost(navController = favouriteNavController, startDestination = FavouriteScreenNavigationLinkList.TOP) {
                    composable(FavouriteScreenNavigationLinkList.TOP) {
                        // トップ
                        Text(text = "top")
                    }
                    composable(FavouriteScreenNavigationLinkList.VideoList) {
                        // 動画リスト
                        Text(text = "video_list")
                    }
                    composable(FavouriteScreenNavigationLinkList.ChannelList) {
                        // ユーザーリスト
                        Text(text = "channel_list")
                    }
                }
            }
        }
    )
}