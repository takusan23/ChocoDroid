package io.github.takusan23.chocodroid.ui.screen.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.chocodroid.ui.component.DownloadContentDeleteButton
import io.github.takusan23.chocodroid.ui.component.ExportDeviceMediaFolderButton
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.viewmodel.VideoListMenuScreenViewModel

/**
 * 動画一覧から開くメニュー。いろいろボタンがあるVer
 *
 * @param viewModel 動画一覧ボトムシートメニューのViewModel。UIと分離すべきなのかな
 * @param videoTitle 動画タイトル
 * @param videoId 動画ID
 * @param folderId お気に入りフォルダ内の動画の場合はフォルダIDを入れてください
 * @param isDownloadContent ダウンロードコンテンツの場合はtrue
 * @param snackbarHostState Snackbarだすやつ
 * @param onClose 閉じてほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListMenuScreen(
    viewModel: VideoListMenuScreenViewModel = viewModel(),
    videoTitle: String,
    videoId: String,
    folderId: Int?,
    isDownloadContent: Boolean,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onClose: () -> Unit,
) {
    VideoListMenuScreen(
        videoTitle = videoTitle,
        snackbarHostState = snackbarHostState,
        content = {

            // お気に入り削除ボタン
            folderId?.let { folderId ->
                FavoriteVideoDeleteButton(
                    snackbarHostState = snackbarHostState,
                    videoId = videoId,
                    folderId = folderId,
                    onDeleteClick = { videoId, folderId ->
                        viewModel.deleteFavoriteVideoItem(videoId, folderId)
                        onClose()
                    }
                )
            }

            // ダウンロードのときのみ
            if (isDownloadContent) {
                ExportDeviceMediaFolderButton {

                }
                DownloadContentDeleteButton(
                    snackbarHostState = snackbarHostState,
                    onDeleteClick = {
                        viewModel.deleteDownloadContent(videoId)
                        onClose()
                    }
                )
            }

        }
    )
}

/**
 * 動画一覧から開くメニューの共通部分。動画タイトルとか
 *
 * @param videoTitle 動画タイトル
 * @param snackbarHostState SnackBarを表示するときに入れて
 * @param content ボトムシートのレイアウト
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListMenuScreen(
    videoTitle: String,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable ColumnScope.() -> Unit,
) {
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
                    fontSize = 25.sp,
                    maxLines = 2
                )
                Divider()
                Column(content = content)
            }
        }
    )
}