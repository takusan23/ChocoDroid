package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.tool.SetNavigationBarColor
import io.github.takusan23.chocodroid.ui.screen.NavigationLinkList


/**
 * 最初の画面に置くボトムナビゲーション
 *
 * @param navHostController メイン画面のNavController
 * */
@Composable
fun HomeScreenBottomNavigation(navHostController: NavHostController) {
    val currentNavRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentNavRoute == NavigationLinkList.FavouriteScreen,
            onClick = { navHostController.navigate(NavigationLinkList.FavouriteScreen, NavigationLinkList.NavOptions) },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_folder_special_24), contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.favourite)) }
        )
        NavigationBarItem(
            selected = currentNavRoute == NavigationLinkList.DownloadScreen,
            onClick = { navHostController.navigate(NavigationLinkList.DownloadScreen, NavigationLinkList.NavOptions) },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_file_download_24), contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.download)) }
        )
        NavigationBarItem(
            selected = currentNavRoute == NavigationLinkList.HistoryScreen,
            onClick = { navHostController.navigate(NavigationLinkList.HistoryScreen, NavigationLinkList.NavOptions) },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_history_24), contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.history)) }
        )
    }
}