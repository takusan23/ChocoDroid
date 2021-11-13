package io.github.takusan23.chocodroid.ui.screen.favourite

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.database.db.FavoriteDB
import io.github.takusan23.chocodroid.ui.component.BackButtonSmallTopBar
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.component.VideoList
import io.github.takusan23.chocodroid.ui.tool.SnackbarComposeTool
import io.github.takusan23.chocodroid.viewmodel.FavoriteVideoListViewModel
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel
import io.github.takusan23.chocodroid.viewmodel.factory.FavoriteVideoListViewModelFactory

/**
 * お気に入りフォルダ内の動画を一覧表示する
 *
 * @param folderId フォルダID
 * @param onBack 戻ってほしいときに呼ばれる
 * */
@ExperimentalMaterial3Api
@Composable
fun FavoriteVideoListScreen(
    mainScreenViewModel: MainScreenViewModel,
    folderId: String,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val application = (context as ComponentActivity).application
    val viewModel = viewModel<FavoriteVideoListViewModel>(factory = FavoriteVideoListViewModelFactory(application, folderId.toInt()))
    val snackbarHostState = remember { SnackbarHostState() }

    val videoList = viewModel.folderVideoList.collectAsState(initial = listOf())
    val folderInfo = viewModel.folderInfo.collectAsState(initial = null)

    M3Scaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            BackButtonSmallTopBar(
                title = { Text(text = folderInfo.value?.folderName ?: "") },
                actions = { TopBarDeleteButton(folderId = folderId.toInt(), snackbarHostState, onBack) },
                onBack = onBack
            )
        },
        content = {
            // ない場合は無いって表示する
            if (videoList.value.isNotEmpty()) {
                VideoList(
                    videoList = videoList.value,
                    onClick = { mainScreenViewModel.loadWatchPage(it) }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = R.string.favorite_folder_video_empty))
                }
            }
        }
    )
}

/**
 * 削除ボタン
 *
 * @param folderId 削除するフォルダID
 * @param snackbarHostState Snackbar表示で使う
 * @param onBack 戻るボタン
 * */
@Composable
private fun TopBarDeleteButton(
    folderId: Int,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    IconButton(
        onClick = {
            // Snackbarで削除していいか聞く
            SnackbarComposeTool.showSnackbar(
                scope = scope,
                snackbarDuration = SnackbarDuration.Long,
                snackbarHostState = snackbarHostState,
                snackbarMessage = context.getString(R.string.delete_message),
                actionLabel = context.getString(R.string.delete),
                onActionPerformed = {
                    deleteFavoriteFolder(context, folderId)
                    onBack()
                }
            )
        },
        content = { Icon(painter = painterResource(id = R.drawable.ic_outline_delete_24), contentDescription = null) }
    )
}

/**
 * お気に入りフォルダを削除する
 *
 * @param context Context
 * @param folderId フォルダID
 * */
private suspend fun deleteFavoriteFolder(context: Context, folderId: Int) {
    val dao = FavoriteDB.getInstance(context).favoriteDao()
    dao.deleteFavoriteFolderFromFolderId(folderId)
}