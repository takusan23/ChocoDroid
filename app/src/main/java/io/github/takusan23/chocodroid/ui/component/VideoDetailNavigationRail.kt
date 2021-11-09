package io.github.takusan23.chocodroid.ui.component

import androidx.compose.material.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.tool.calcM3ElevationColor
import io.github.takusan23.chocodroid.ui.screen.videodetail.VideoDetailNavigationLinkList
import io.github.takusan23.internet.data.watchpage.WatchPageData

/**
 * 動画詳細画面で使ってるナビゲーションレール
 *
 * @param navHostController 今表示してる画面、画面遷移で使う
 * @param watchPageData 視聴ページデータ
 * */
@Composable
fun VideoDetailNavigationRail(navHostController: NavHostController, watchPageData: WatchPageData) {
    // 現在表示されてる画面のルート名
    val currentNavRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route
    // BottomNavigationの色を取得する
    val calcColor = calcM3ElevationColor(
        colorScheme = MaterialTheme.colorScheme,
        color = MaterialTheme.colorScheme.surface,
        elevation = 3.dp
    )

    NavigationRail(containerColor = calcColor) {
        NavigationRailItem(
            selected = currentNavRoute == VideoDetailNavigationLinkList.VideoDetailDescriptionScreen,
            icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_info_24), contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.video_info)) },
            onClick = { navHostController.navigate(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen, navOptions { this.popUpTo(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) }) }
        )
        NavigationRailItem(
            selected = currentNavRoute == VideoDetailNavigationLinkList.VideoDetailMenuScreen,
            icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_more_vert_24), contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.video_menu)) },
            onClick = { navHostController.navigate(VideoDetailNavigationLinkList.VideoDetailMenuScreen, navOptions { this.popUpTo(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) }) }
        )
        NavigationRailItem(
            selected = currentNavRoute == VideoDetailNavigationLinkList.VideoDetailRelatedVideos,
            icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_art_track_24), contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.related_videos)) },
            onClick = { navHostController.navigate(VideoDetailNavigationLinkList.VideoDetailRelatedVideos, navOptions { this.popUpTo(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) }) }
        )
        // 生放送では保存無効
        if (!watchPageData.isLiveStream()) {
            NavigationRailItem(
                selected = currentNavRoute == VideoDetailNavigationLinkList.VideoDetailDownloadScreen,
                icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_file_download_24), contentDescription = null) },
                label = { Text(text = stringResource(id = R.string.download_alt)) },
                onClick = { navHostController.navigate(VideoDetailNavigationLinkList.VideoDetailDownloadScreen, navOptions { this.popUpTo(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) }) }
            )
        }
    }
}