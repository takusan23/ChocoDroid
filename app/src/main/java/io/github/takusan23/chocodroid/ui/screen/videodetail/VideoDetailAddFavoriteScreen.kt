package io.github.takusan23.chocodroid.ui.screen.videodetail

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.database.db.FavoriteDB
import io.github.takusan23.chocodroid.database.entity.FavoriteVideoDBEntity
import io.github.takusan23.chocodroid.ui.component.FavoriteFolderList
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.tool.SnackbarComposeTool
import io.github.takusan23.internet.data.CommonVideoData
import io.github.takusan23.internet.data.watchpage.WatchPageData
import io.github.takusan23.internet.data.watchpage.WatchPageResponseJSONData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * お気に入りに追加する画面。
 *
 * 生放送時は利用できません。
 *
 * @param watchPageData 視聴ページ情報
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailAddFavoriteScreen(watchPageData: WatchPageData) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val databaseDAO = remember { FavoriteDB.getInstance(context).favoriteDao() }
    // フォルダ一覧を取得
    val favoriteFolderList = databaseDAO.getAllFavVideoFolder().collectAsState(initial = listOf())

    M3Scaffold(
        snackbarHostState = snackbarHostState,
        content = {
            Column {
                Text(
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp,
                    text = stringResource(id = R.string.add_favourite_list),
                )

                // ない場合は無いって表示する
                if (favoriteFolderList.value.isNotEmpty()) {
                    FavoriteFolderList(
                        modifier = Modifier.fillMaxSize(),
                        list = favoriteFolderList.value,
                        onClick = { folderId ->
                            // DBへ追加していいか聞く
                            SnackbarComposeTool.showSnackbar(
                                scope = scope,
                                snackbarHostState = snackbarHostState,
                                snackbarMessage = context.getString(R.string.add_video_message),
                                actionLabel = context.getString(R.string.add),
                                onActionPerformed = {
                                    // 追加
                                    val isAdded = addVideoToFavoriteFolder(
                                        context = context,
                                        folderId = folderId,
                                        watchPageResponseJSONData = watchPageData.watchPageResponseJSONData
                                    )
                                    val toastMessage = if (isAdded) context.getString(R.string.add_successful) else context.getString(R.string.already_added)
                                    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                                },
                            )
                        }
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
 * お気に入りフォルダへ動画を登録する
 *
 * 生放送時は利用できません。
 *
 * @param context Context
 * @param folderId フォルダID
 * @param watchPageResponseJSONData 視聴ページの中にあるJSON
 * @return 既に追加済みの場合はfalseを返します
 * */
private suspend fun addVideoToFavoriteFolder(
    context: Context,
    folderId: Int,
    watchPageResponseJSONData: WatchPageResponseJSONData,
) = withContext(Dispatchers.IO) {
    // データベース
    val favoriteDB = FavoriteDB.getInstance(context)
    // 使いやすいようデータクラスへ変換
    val commonVideoData = CommonVideoData(watchPageResponseJSONData)
    // 既に追加済み？
    val isExists = favoriteDB.favoriteDao().isExistsVideoItemFromFolderId(folderId, commonVideoData.videoId)
    if (isExists) return@withContext false
    // 追加
    favoriteDB.favoriteDao().insert(FavoriteVideoDBEntity(
        folderId = folderId,
        videoId = commonVideoData.videoId,
        title = commonVideoData.videoTitle,
        thumbnailUrl = commonVideoData.thumbnailUrl,
        publishedDate = commonVideoData.publishDate!!,
        ownerName = commonVideoData.ownerName,
        insertDate = System.currentTimeMillis(),
        duration = commonVideoData.duration!!,
    ))
    return@withContext true
}
