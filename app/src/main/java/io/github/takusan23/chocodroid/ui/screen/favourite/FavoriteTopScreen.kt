package io.github.takusan23.chocodroid.ui.screen.favourite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.CreateFavoriteFolderItem
import io.github.takusan23.chocodroid.ui.component.FavoriteChCarouselItem
import io.github.takusan23.chocodroid.ui.component.FavoriteFolderVideoCarouselItem
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.viewmodel.FavoriteFolderScreenViewModel

/**
 * お気に入りフォルダ一覧表示画面
 *
 * @param viewModel [FavoriteFolderScreenViewModel]
 * @param onChannelClick お気に入りチャンネル一覧のアイコンを押したときに呼ばれます
 * @param onNavigate 画面遷移をしてほしいときに呼ばれます
 * @param onVideoLoad 動画を読み込んでほしいときに呼ばれます。動画IDが渡されます
 * @param onAddClick 追加ボタンを押したら呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteTopScreen(
    viewModel: FavoriteFolderScreenViewModel = viewModel(),
    onAddClick: () -> Unit,
    onChannelClick: (String) -> Unit,
    onVideoLoad: (String) -> Unit,
    onNavigate: (String) -> Unit,
) {
    /** フォルダの名前を入れた動画データクラス */
    val favoriteFolderVideoMap = viewModel.favoriteFolderVideoMap.collectAsState(initial = mapOf())

    /** お気に入りチャンネルの一覧 */
    val favoriteChList = viewModel.favoriteChList.collectAsState(initial = null)

    M3Scaffold {
        LazyColumn {
            // お気に入りチャンネルのカルーセル
            if (favoriteChList.value?.isNotEmpty() == true) {
                item {
                    FavoriteChCarouselItem(
                        channelList = favoriteChList.value!!,
                        onChannelClick = onChannelClick,
                        onLabelClick = { onNavigate(FavouriteScreenNavigationLinkList.ChannelList) },
                    )
                }
            }
            // お気に入りフォルダーのカルーセル
            if (favoriteFolderVideoMap.value.isNotEmpty()) {
                favoriteFolderVideoMap.value.forEach { item ->
                    item {
                        FavoriteFolderVideoCarouselItem(
                            folderName = item.key.folderName,
                            folderId = item.key.id,
                            favoriteVideoList = item.value.mapNotNull { it.convertToCommonVideoData() },
                            onLabelClick = { onNavigate(FavouriteScreenNavigationLinkList.getFolderVideoList(it.toString())) },
                            onVideoClick = onVideoLoad
                        )
                    }
                }
            } else {
                // 無いとき
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text(text = stringResource(id = R.string.favorite_folder_empty)) }
                }
            }
            // 追加するボタン
            item {
                CreateFavoriteFolderItem(
                    modifier = Modifier.padding(top = 20.dp, start = 10.dp),
                    onClick = onAddClick
                )
            }
        }
    }
}