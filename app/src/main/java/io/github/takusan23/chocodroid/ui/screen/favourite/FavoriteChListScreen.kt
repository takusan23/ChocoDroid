package io.github.takusan23.chocodroid.ui.screen.favourite

import FavoriteChList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.database.entity.FavoriteChDBEntity
import io.github.takusan23.chocodroid.ui.component.BackButtonSmallTopBar
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.viewmodel.FavoriteChListViewModel

/**
 * チャンネル一覧画面
 *
 * @param viewModel お気に入りチャンネル画面ViewModel
 * @param onBack 戻ってほしいときに呼ばれる
 * @param onChannelClick チャンネル画面に切り替えてほしいときに呼ばれる
 * @param onMenuClick メニュー押したときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteChListScreen(
    viewModel: FavoriteChListViewModel = viewModel(),
    onBack: () -> Unit,
    onChannelClick: (String) -> Unit,
    onMenuClick: (FavoriteChDBEntity) -> Unit,
) {
    val favoriteChList = viewModel.favoriteChList.collectAsState(initial = listOf())

    M3Scaffold(
        topBar = {
            BackButtonSmallTopBar(
                title = { Text(text = stringResource(id = R.string.favorite_channel)) },
                onBack = onBack
            )
        },
        content = {
            FavoriteChList(
                channelList = favoriteChList.value,
                onClick = onChannelClick,
                onMenuClick = onMenuClick
            )
        }
    )
}