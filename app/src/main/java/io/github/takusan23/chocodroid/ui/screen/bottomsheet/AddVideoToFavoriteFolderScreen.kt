package io.github.takusan23.chocodroid.ui.screen.bottomsheet

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.database.entity.FavoriteFolderDBEntity
import io.github.takusan23.chocodroid.ui.component.FavoriteFolderList
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.tool.SnackbarComposeTool
import io.github.takusan23.chocodroid.viewmodel.AddVideoToFavoriteFolderViewModel
import io.github.takusan23.internet.data.CommonVideoData

/**
 * 動画をお気に入りフォルダに追加する画面
 *
 * @param initData 表示の際に渡すデータクラス
 * */
@Composable
fun AddVideoToFavoriteFolderScreen(
    viewModel: AddVideoToFavoriteFolderViewModel = viewModel(),
    initData: AddVideoToFavoriteFolderScreenInitData,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    // フォルダ一覧を取得
    val favoriteFolderList = viewModel.favoriteFolderList.collectAsState(initial = listOf())

    AddVideoToFavoriteFolderScreen(
        snackbarHostState = snackbarHostState,
        favoriteFolderList = favoriteFolderList.value
    ) { folderId ->
        // DBへ追加していいか聞く
        SnackbarComposeTool.showSnackbar(
            scope = scope,
            snackbarHostState = snackbarHostState,
            snackbarMessage = context.getString(R.string.add_video_message),
            actionLabel = context.getString(R.string.add),
            onActionPerformed = {
                // 追加
                val isAdded = viewModel.addVideoToFavoriteFolder(
                    folderId = folderId,
                    commonVideoData = initData.commonVideoData
                )
                val toastMessage = if (isAdded) context.getString(R.string.add_successful) else context.getString(R.string.already_added)
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            },
        )
    }
}

/**
 * 動画をお気に入りフォルダに追加する画面
 *
 * @param snackbarHostState スナックバー出すやつ
 * @param favoriteFolderList お気に入りフォルダ一覧
 * @param onAddClick 押したとき
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddVideoToFavoriteFolderScreen(
    snackbarHostState: SnackbarHostState,
    favoriteFolderList: List<FavoriteFolderDBEntity>,
    onAddClick: (Int) -> Unit,
) {
    M3Scaffold(
        modifier = Modifier.fillMaxHeight(0.5f),
        snackbarHostState = snackbarHostState,
        content = {
            Column {
                Text(
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp,
                    text = stringResource(id = R.string.add_favourite_list),
                )
                // ない場合は無いって表示する
                if (favoriteFolderList.isNotEmpty()) {
                    FavoriteFolderList(
                        modifier = Modifier.fillMaxSize(),
                        list = favoriteFolderList,
                        onClick = { folderId -> onAddClick(folderId) }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text(text = stringResource(id = R.string.favorite_folder_empty)) }
                }
            }
        },
    )
}

/**
 * [AddVideoToFavoriteFolderScreen]を表示する際に使うデータをまとめたデータクラス
 *
 * @param commonVideoData 動画情報
 * */
data class AddVideoToFavoriteFolderScreenInitData(
    val commonVideoData: CommonVideoData,
) : BottomSheetInitData(BottomSheetScreenList.AddVideoToFavoriteFolder)