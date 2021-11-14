package io.github.takusan23.chocodroid.ui.screen.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.database.db.FavoriteDB
import io.github.takusan23.chocodroid.ui.component.LeftStartTextButton
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.tool.SnackbarComposeTool

/**
 * 動画一覧から開くメニュー
 * 
 * お気に入りフォルダから削除とか
 *
 * @param videoId 動画ID
 * @param videoTitle 動画タイトル
 * @param folderId お気に入りフォルダ内の動画の場合はフォルダIDを入れてください
 * @param onClose 閉じるとき？
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListMenuScreen(
    videoTitle: String,
    videoId: String,
    folderId: Int?,
    onClose: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    M3Scaffold(
        modifier = Modifier.fillMaxHeight(0.5f),
        snackbarHostState = snackbarHostState,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = videoTitle,
                    fontSize = 25.sp
                )
                Divider()

                // お気に入り削除ボタン
                folderId?.let { folderId ->
                    FavoriteVideoDeleteButton(
                        snackbarHostState = snackbarHostState,
                        videoId = videoId,
                        folderId = folderId,
                        onClose = onClose,
                    )
                }
            }
        }
    )
}

/**
 * お気に入りから動画を削除するボタン
 *
 * @param snackbarHostState スナックバー管理
 * @param videoId 動画ID
 * @param folderId フォルダID
 * @param onClose 閉じてほしいときに呼ばれます
 * */
@Composable
fun FavoriteVideoDeleteButton(
    snackbarHostState: SnackbarHostState,
    videoId: String,
    folderId: Int,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val database = remember { FavoriteDB.getInstance(context) }
    val scope = rememberCoroutineScope()

    LeftStartTextButton(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        onClick = {
            SnackbarComposeTool.showSnackbar(
                scope = scope,
                snackbarHostState = snackbarHostState,
                snackbarMessage = context.getString(R.string.delete_message),
                actionLabel = context.getString(R.string.delete),
                onActionPerformed = {
                    database.favoriteDao().deleteVideoItem(folderId = folderId, videoId = videoId)
                    onClose()
                }
            )
        },
        content = {
            Icon(painter = painterResource(id = R.drawable.ic_outline_delete_24), contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.video_list_menu_delete_favorite))
        }
    )
}
