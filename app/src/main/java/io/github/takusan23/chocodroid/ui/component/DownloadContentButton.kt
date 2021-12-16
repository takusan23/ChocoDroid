package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.tool.SnackbarComposeTool

/**
 * 削除ボタン
 *
 * @param modifier [Modifier]
 * @param snackbarHostState Snackbar表示のために
 * @param onDeleteClick 削除ボタン押したら呼ばれます
 * */
@Composable
fun DownloadContentDeleteButton(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onDeleteClick: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LeftStartTextButton(
        modifier = modifier.fillMaxWidth(),
        onClick = {
            SnackbarComposeTool.showSnackbar(
                scope = scope,
                snackbarHostState = snackbarHostState,
                snackbarMessage = context.getString(R.string.delete_message),
                actionLabel = context.getString(R.string.delete),
                snackbarDuration = SnackbarDuration.Long,
                onActionPerformed = onDeleteClick
            )
        },
        content = {
            Icon(painter = painterResource(id = R.drawable.ic_outline_delete_24), contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.video_list_menu_delete_download))
        }
    )
}

/**
 * 動画一覧から開くメニューのダウンロードボタン
 *
 * @param modifier [Modifier]
 * @param onClick 押したとき
 * */
@Composable
fun DownloadButton(
    modifier: Modifier,
    onClick:()->Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LeftStartTextButton(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        content = {
            Icon(painter = painterResource(id = R.drawable.ic_outline_file_download_24), contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.download))
        }
    )
}

