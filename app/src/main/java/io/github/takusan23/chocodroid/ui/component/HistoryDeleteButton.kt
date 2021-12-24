package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.tool.SnackbarComposeTool

/**
 * 履歴全件削除ボタン。SnackBarつき
 *
 * @param modifier [Modifier]
 * @param snackbarHostState SnackBar表示用
 * @param onDelete 削除押したときに呼ばれる
 * */
@Composable
fun HistoryAllDeleteTextButton(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    onDelete: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // 消すボタン
    TextButton(
        modifier = modifier,
        onClick = {
            SnackbarComposeTool.showSnackbar(
                scope = scope,
                snackbarDuration = SnackbarDuration.Long,
                snackbarHostState = snackbarHostState,
                snackbarMessage = context.getString(R.string.delete_message),
                actionLabel = context.getString(R.string.delete),
                onActionPerformed = onDelete
            )
        },
        content = {
            Icon(painter = painterResource(id = R.drawable.ic_outline_delete_24), contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.delete_all))
        },
    )
}

/**
 * 一件だけ履歴削除。SnackBarつき
 *
 * @param modifier [Modifier]
 * @param snackbarHostState SnackBar表示用
 * @param onDelete 削除押したときに呼ばれる
 * */
@Composable
fun HistoryDeleteButton(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    onDelete: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LeftStartTextButton(
        modifier = modifier.fillMaxWidth(),
        onClick = {
            SnackbarComposeTool.showSnackbar(
                scope = scope,
                snackbarDuration = SnackbarDuration.Long,
                snackbarHostState = snackbarHostState,
                snackbarMessage = context.getString(R.string.delete_message),
                actionLabel = context.getString(R.string.delete),
                onActionPerformed = onDelete
            )
        },
        content = {
            Icon(painter = painterResource(id = R.drawable.ic_outline_delete_24), contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.video_list_menu_delete_history))
        }
    )
}