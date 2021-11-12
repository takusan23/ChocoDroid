package io.github.takusan23.chocodroid.ui.screen.favourite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.database.entity.FavoriteFolderDBEntity
import io.github.takusan23.chocodroid.ui.component.FavoriteFolderList
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.ChocoDroidBottomSheetNavigationLinkList
import io.github.takusan23.chocodroid.viewmodel.FavoriteFolderScreenViewModel

/**
 * お気に入りフォルダ一覧表示画面
 *
 * @param viewModel [FavoriteFolderScreenViewModel]
 * @param onVideoListNavigate 項目押したら呼ばれる。[FavoriteFolderDBEntity.id]を入れます
 * @param onFabClick Fabを押したら呼ばれる。[ChocoDroidBottomSheetNavigationLinkList.AddFavoriteFolder]が入ります
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteFolderScreen(
    viewModel: FavoriteFolderScreenViewModel = viewModel(),
    onFabClick: (String) -> Unit,
    onVideoListNavigate: (Int) -> Unit,
) {
    /** フォルダ一覧 */
    val favoriteFolderList = viewModel.favoriteFolderList.collectAsState(initial = listOf())

    M3Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            LargeFloatingActionButton(onClick = { onFabClick(ChocoDroidBottomSheetNavigationLinkList.AddFavoriteFolder) }) {
                Icon(painter = painterResource(id = R.drawable.ic_outline_create_24), contentDescription = null)
            }
        },
        content = {
            if (favoriteFolderList.value.isNotEmpty()) {
                FavoriteFolderList(
                    list = favoriteFolderList.value,
                    onClick = onVideoListNavigate
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = R.string.favorite_folder_empty))
                }
            }
        }
    )


}