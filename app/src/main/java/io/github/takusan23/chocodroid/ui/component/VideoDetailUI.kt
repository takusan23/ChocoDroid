package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.videodetail.VideoDetailDescriptionScreen
import io.github.takusan23.chocodroid.ui.component.videodetail.VideoDetailDownloadScreen
import io.github.takusan23.chocodroid.ui.component.videodetail.VideoDetailMenuScreen
import io.github.takusan23.chocodroid.ui.component.videodetail.VideoDetailNavigationLinkList
import io.github.takusan23.htmlparse.data.watchpage.WatchPageData
import io.github.takusan23.htmlparse.html.WatchPageHTML

/**
 * 動画説明部分のUI
 *
 * @param watchPageData 視聴ページレスポンスデータ
 * @param navHostController 動画状とかメニュー切り替えよう
 * */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun VideoDetailUI(
    watchPageData: WatchPageData,
    navHostController: NavHostController = rememberNavController(),
) {
    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            NavigationRail(
                backgroundColor = MaterialTheme.colors.primary,
                elevation = 0.dp,
                content = {
                    NavigationRailItem(
                        selected = false,
                        icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_info_24), contentDescription = null) },
                        label = { Text(text = stringResource(id = R.string.video_info)) },
                        onClick = { navHostController.navigate(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen, navOptions { this.popUpTo(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) }) }
                    )
                    NavigationRailItem(
                        selected = false,
                        icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_more_vert_24), contentDescription = null) },
                        label = { Text(text = stringResource(id = R.string.video_menu)) },
                        onClick = { navHostController.navigate(VideoDetailNavigationLinkList.VideoDetailMenuScreen, navOptions { this.popUpTo(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) }) }
                    )
                    NavigationRailItem(
                        selected = false,
                        icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_file_download_24), contentDescription = null) },
                        label = { Text(text = stringResource(id = R.string.download_alt)) },
                        onClick = { navHostController.navigate(VideoDetailNavigationLinkList.VideoDetailDownloadScreen, navOptions { this.popUpTo(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) }) }
                    )
                }
            )
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp),
            ) {
                NavHost(navController = navHostController, startDestination = VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) {
                    composable(VideoDetailNavigationLinkList.VideoDetailDescriptionScreen) {
                        VideoDetailDescriptionScreen(watchPageData = watchPageData)
                    }
                    composable(VideoDetailNavigationLinkList.VideoDetailMenuScreen) {
                        VideoDetailMenuScreen(watchPageData = watchPageData)
                    }
                    composable(VideoDetailNavigationLinkList.VideoDetailDownloadScreen) {
                        VideoDetailDownloadScreen(watchPageData = watchPageData)
                    }
                }
            }
        }
    }
}

