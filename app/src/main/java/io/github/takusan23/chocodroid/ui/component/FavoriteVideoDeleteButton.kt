package io.github.takusan23.chocodroid.ui.screen.bottomsheet

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.database.db.FavoriteDB
import io.github.takusan23.chocodroid.ui.component.LeftStartTextButton
import io.github.takusan23.chocodroid.ui.tool.SnackbarComposeTool

/**
 * お気に入りから動画を削除するボタン
 *
 * @param snackbarHostState スナックバー管理
 * @param videoId 動画ID
 * @param folderId フォルダID
 * @param onDeleteClick 削除実行時に呼ばれる。[videoId]と[folderId]が渡されます
 * */
@Composable
fun FavoriteVideoDeleteButton(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    videoId: String,
    folderId: Int,
    onDeleteClick: suspend (String, Int) -> Unit,
) {
    val context = LocalContext.current
    val database = remember { FavoriteDB.getInstance(context) }
    val scope = rememberCoroutineScope()

    LeftStartTextButton(
        modifier = modifier.fillMaxWidth(),
        onClick = {
            SnackbarComposeTool.showSnackbar(
                scope = scope,
                snackbarHostState = snackbarHostState,
                snackbarMessage = context.getString(R.string.delete_message),
                actionLabel = context.getString(R.string.delete),
                onActionPerformed = { onDeleteClick(videoId, folderId) },
            )
        },
        content = {
            Icon(painter = painterResource(id = R.drawable.ic_outline_delete_24), contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.video_list_menu_delete_favorite))
        }
    )
}
