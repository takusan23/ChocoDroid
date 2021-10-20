package io.github.takusan23.chocodroid.ui.component

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.videodetail.VideoDetailNavigationLinkList
import io.github.takusan23.htmlparse.data.watchpage.WatchPageData

/**
 * 動画詳細画面で使ってるナビゲーションレール
 *
 * @param navHostController 今表示してる画面、画面遷移で使う
 * @param watchPageData 視聴ページデータ
 * */
@ExperimentalMaterialApi
@Composable
fun VideoDetailNavigationRail(navHostController: NavHostController, watchPageData: WatchPageData) {
    // 現在表示されてる画面のルート名
    val currentNavRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route

    NavigationRail(
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 0.dp,
        content = {
            NavigationRailItem(
                selected = currentNavRoute == VideoDetailNavigationLinkList.VideoDetailDescriptionScreen,
                icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_info_24), contentDescription = null) },
                label = { Text(text = stringResource(id = R.string.video_info)) },
                selectedContentColor = MaterialTheme.colors.primaryVariant,
                onClick = { navHostController.navigate(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen, navOptions { this.popUpTo(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) }) }
            )
            NavigationRailItem(
                selected = currentNavRoute == VideoDetailNavigationLinkList.VideoDetailMenuScreen,
                icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_more_vert_24), contentDescription = null) },
                label = { Text(text = stringResource(id = R.string.video_menu)) },
                selectedContentColor = MaterialTheme.colors.primaryVariant,
                onClick = { navHostController.navigate(VideoDetailNavigationLinkList.VideoDetailMenuScreen, navOptions { this.popUpTo(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) }) }
            )
            NavigationRailItem(
                selected = currentNavRoute == VideoDetailNavigationLinkList.VideoDetailRelatedVideos,
                icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_art_track_24), contentDescription = null) },
                label = { Text(text = stringResource(id = R.string.related_videos)) },
                selectedContentColor = MaterialTheme.colors.primaryVariant,
                onClick = { navHostController.navigate(VideoDetailNavigationLinkList.VideoDetailRelatedVideos, navOptions { this.popUpTo(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) }) }
            )
            // 生放送では保存無効
            if (!watchPageData.isLiveStream()) {
                NavigationRailItem(
                    selected = currentNavRoute == VideoDetailNavigationLinkList.VideoDetailDownloadScreen,
                    icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_file_download_24), contentDescription = null) },
                    label = { Text(text = stringResource(id = R.string.download_alt)) },
                    selectedContentColor = MaterialTheme.colors.primaryVariant,
                    onClick = { navHostController.navigate(VideoDetailNavigationLinkList.VideoDetailDownloadScreen, navOptions { this.popUpTo(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) }) }
                )
            }
        }
    )
}