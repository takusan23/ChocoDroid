package io.github.takusan23.chocodroid.ui.tool

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object SnackbarComposeTool {

    /**
     * Snackbarを表示する。
     *
     * @param snackbarDuration SnackBarの表示時間
     * @param snackbarHostState ScaffoldStateにあるやつ
     * @param snackbarMessage Snackbarのメッセージ
     * @param actionLabel Snackbarのボタンのテキスト
     * @param onActionPerformed ボタン押したとき
     * @param onDismissed ボタン押さずに消したとき
     * */
    fun showSnackbar(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        snackbarMessage: String,
        actionLabel: String,
        snackbarDuration: SnackbarDuration = SnackbarDuration.Indefinite,
        onActionPerformed: () -> Unit,
        onDismissed: () -> Unit = {},
    ) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(snackbarMessage, actionLabel, snackbarDuration)
            if (result == SnackbarResult.ActionPerformed) {
                onActionPerformed()
            } else {
                onDismissed()
            }
        }
    }
}

