package io.github.takusan23.chocodroid.ui.component

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.screen.NavigationLinkList

/**
 * 最初の画面に置くボトムナビゲーション
 *
 * @param navHostController メイン画面のNavController
 * */
@Composable
fun HomeScreenBottomNavigation(navHostController: NavHostController) {
    BottomNavigation(
        contentColor = MaterialTheme.colors.primaryVariant,
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    ) {
        BottomNavigationItem(
            selected = false,
            onClick = { navHostController.navigate(NavigationLinkList.FavouriteScreen, navOptions { popUpTo(NavigationLinkList.FavouriteScreen) }) },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_folder_special_24), contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.favourite)) }
        )
        BottomNavigationItem(
            selected = false,
            onClick = { navHostController.navigate(NavigationLinkList.DownloadScreen, navOptions { popUpTo(NavigationLinkList.FavouriteScreen) }) },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_file_download_24), contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.download)) }
        )
        BottomNavigationItem(
            selected = false,
            onClick = { navHostController.navigate(NavigationLinkList.HistoryScreen, navOptions { popUpTo(NavigationLinkList.FavouriteScreen) }) },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_history_24), contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.history)) }
        )
    }
}