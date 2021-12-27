package io.github.takusan23.chocodroid.ui.screen.favourite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.FavoriteChCarouselItem
import io.github.takusan23.chocodroid.ui.component.FavoriteFolderVideoCarouselItem
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.viewmodel.FavoriteFolderScreenViewModel

/**
 * お気に入りフォルダ一覧表示画面
 *
 * @param viewModel [FavoriteFolderScreenViewModel]
 * @param onChannelClick お気に入りチャンネル一覧のアイコンを押したときに呼ばれます。
 * @param onNavigate 画面遷移をしてほしいときに呼ばれます
 * @param onVideoLoad 動画を読み込んでほしいときに呼ばれます。動画IDが渡されます
 * @param onFabClick Fabを押したら呼ばれる。
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteTopScreen(
    viewModel: FavoriteFolderScreenViewModel = viewModel(),
    onFabClick: () -> Unit,
    onChannelClick: (String) -> Unit,
    onVideoLoad: (String) -> Unit,
    onNavigate: (String) -> Unit,
) {
    /** フォルダの名前を入れた動画データクラス */
    val favoriteFolderVideoMap = viewModel.favoriteFolderVideoMap.collectAsState(initial = mapOf())

    /** お気に入りチャンネルの一覧 */
    val favoriteChList = viewModel.favoriteChList.collectAsState(initial = null)

    M3Scaffold(
        floatingActionButton = {
            SmallFloatingActionButton(onClick = onFabClick) {
                Icon(painter = painterResource(id = R.drawable.ic_outline_create_24), contentDescription = null)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = {
            if (favoriteFolderVideoMap.value.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.padding(bottom = 20.dp),
                    content = {
                        // お気に入りチャンネルのカルーセル
                        if (favoriteChList.value != null) {
                            item {
                                FavoriteChCarouselItem(
                                    channelList = favoriteChList.value!!,
                                    onChannelClick = onChannelClick,
                                    onLabelClick = { onNavigate(FavouriteScreenNavigationLinkList.ChannelList) },
                                )
                            }
                        }
                        // --- キリトリセン ---
                        item {
                            Divider()
                        }
                        // お気に入りフォルダーのカルーセル
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
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text(text = stringResource(id = R.string.favorite_folder_empty)) }
            }
        }
    )


}