package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Tab
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.ui.screen.favourite.FavouriteScreenNavigationLinkList
import kotlin.math.max

/**
 * お気に入り画面のTab
 *
 * @param navHostController 画面遷移で使うやつ
 * */
@Composable
fun FavouriteTab(navHostController: NavHostController) {
    // タブ遷移先配列
    val tabRouteList = listOf(
        FavouriteScreenNavigationLinkList.TOP,
        FavouriteScreenNavigationLinkList.VideoList,
        FavouriteScreenNavigationLinkList.ChannelList,
    )
    // タブのラベル
    val tabLabelList = listOf(
        "トップ",
        "動画リスト",
        "チャンネルリスト"
    )
    // 現在選択中の位置
    val currentBackStackEntry = navHostController.currentBackStackEntryFlow.collectAsState(initial = null)
    val currentSelectPos = max(0, tabRouteList.indexOfFirst { label -> label == currentBackStackEntry.value?.destination?.route })

    // タブ
    Material3TabLayout(
        modifier = Modifier.fillMaxWidth(),
        selectIndex = currentSelectPos,
        tabs = {
            tabLabelList.forEachIndexed { index, label ->
                Tab(
                    selected = currentSelectPos == index,
                    onClick = { navHostController.navigate(tabRouteList[index]) },
                    content = {
                        Text(
                            modifier = Modifier.padding(top = 15.dp, bottom = 10.dp),
                            text = label,
                            style = TextStyle(color = if (currentSelectPos == index) MaterialTheme.colorScheme.primary else LocalTextStyle.current.color)
                        )
                    }
                )
            }
        }
    )
}