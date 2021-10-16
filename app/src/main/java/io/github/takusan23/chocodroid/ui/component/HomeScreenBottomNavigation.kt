package io.github.takusan23.chocodroid.ui.component

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.chocodroid.R

/**
 * 最初の画面に置くボトムナビゲーション
 * */
@Composable
fun HomeScreenBottomNavigation() {
    BottomNavigation(
        contentColor = MaterialTheme.colors.primaryVariant,
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    ) {
        BottomNavigationItem(selected = false, onClick = { }, icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_folder_special_24), contentDescription = "お気に入り") }, label = { Text(text = "お気に入り") })
        BottomNavigationItem(selected = false, onClick = { }, icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_file_download_24), contentDescription = "ダウンロード") }, label = { Text(text = "キャッシュ") })
        BottomNavigationItem(selected = false, onClick = { }, icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_history_24), contentDescription = "履歴") }, label = { Text(text = "履歴") })
    }
}