package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.AddFavoriteFolderScreenInitData
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.BottomSheetInitData
import io.github.takusan23.chocodroid.ui.screen.favourite.FavoriteChListScreen
import io.github.takusan23.chocodroid.ui.screen.favourite.FavoriteTopScreen
import io.github.takusan23.chocodroid.ui.screen.favourite.FavoriteVideoListScreen
import io.github.takusan23.chocodroid.ui.screen.favourite.FavouriteScreenNavigationLinkList

/**
 * お気に入り画面
 *
 * @param navController メイン画面のNavController
 * @param onLoadWatchPage 動画を読み込んでほしいときに呼ばれます。動画IDが渡されます
 * @param onBottomSheetNavigate BottomSheetの表示と画面遷移してほしいときに呼ばれる
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    navController: NavHostController,
    onLoadWatchPage: (String) -> Unit,
    onBottomSheetNavigate: (BottomSheetInitData) -> Unit,
) {
    // 画面遷移
    val favouriteNavController = rememberNavController()

    M3Scaffold(
        topBar = {
            ChocoBridgeBar(
                onClick = { navController.navigate(NavigationLinkList.getChocoDroidBridgeSearchScreen()) },
                onSettingClick = { navController.navigate(NavigationLinkList.SettingScreen) }
            )
        },
        content = {
            Column {
                // ルーティング
                NavHost(navController = favouriteNavController, startDestination = FavouriteScreenNavigationLinkList.FavoriteTop) {
                    composable(FavouriteScreenNavigationLinkList.FavoriteTop) {
                        // フォルダ、チャンネル一覧
                        FavoriteTopScreen(
                            onNavigate = { route -> favouriteNavController.navigate(route) },
                            onAddClick = { onBottomSheetNavigate(AddFavoriteFolderScreenInitData()) },
                            onVideoLoad = onLoadWatchPage,
                            onChannelClick = { channelId -> navController.navigate(NavigationLinkList.getChannelScreenLink(channelId)) }
                        )
                    }
                    composable(FavouriteScreenNavigationLinkList.getFolderVideoList("{folder_id}")) { backStackEntry ->
                        // フォルダの中身。動画一覧
                        val folderId = backStackEntry.arguments?.getString("folder_id")!!.toInt()
                        FavoriteVideoListScreen(
                            folderId = folderId,
                            onBottomSheetNavigate = onBottomSheetNavigate,
                            onBack = { favouriteNavController.popBackStack() },
                            onVideoLoad = onLoadWatchPage
                        )
                    }
                    composable(FavouriteScreenNavigationLinkList.ChannelList) {
                        // チャンネル一覧
                        FavoriteChListScreen(
                            onBack = { favouriteNavController.popBackStack() },
                            onChannelClick = { channelId -> navController.navigate(NavigationLinkList.getChannelScreenLink(channelId)) },
                            onMenuClick = { }
                        )
                    }
                }
            }
        }
    )
}