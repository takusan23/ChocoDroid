package io.github.takusan23.chocodroid.ui.screen.bottomsheet

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.data.DownloadRequestData
import io.github.takusan23.chocodroid.service.ContentDownloadService
import io.github.takusan23.chocodroid.ui.component.*
import io.github.takusan23.chocodroid.viewmodel.VideoListMenuScreenViewModel
import kotlinx.coroutines.launch

/**
 * 動画一覧から開くメニュー。いろいろボタンがあるVer
 *
 * @param viewModel Composeにデータベース処理とか書いていいの？よくわからんからViewModelに書いてる
 * @param data メニューに渡すデータクラス。[ChocoDroidBottomSheetNavigationLinkList.getVideoListMenu]等を参照
 * @param snackbarHostState Snackbarだすやつ
 * @param onClose 閉じてほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListMenuScreen(
    viewModel: VideoListMenuScreenViewModel = viewModel(),
    data: VideoListMenuData,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onClose: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val paddingModifier = Modifier.padding(start = 5.dp, top = 5.dp, end = 5.dp)

    VideoListMenuScreen(
        videoTitle = data.videoTitle,
        snackbarHostState = snackbarHostState,
        content = {

            if (data.isDownloadContent) {
                // ダウンロードのときのみ
                ExportDeviceMediaFolderButton(
                    modifier = paddingModifier,
                    onClick = {
                        scope.launch {
                            viewModel.copyFileToVideoOrMusicFolder(data.videoId)
                            Toast.makeText(context, context.getString(R.string.copy_successful), Toast.LENGTH_SHORT).show()
                            onClose()
                        }
                    }
                )
                DownloadContentDeleteButton(
                    modifier = paddingModifier,
                    snackbarHostState = snackbarHostState,
                    onDeleteClick = {
                        viewModel.deleteDownloadContent(data.videoId)
                        onClose()
                    }
                )
            } else {
                // ダウンロードサービス起動
                DownloadButton(
                    modifier = paddingModifier,
                    onClick = {
                        ContentDownloadService.startDownloadService(context, DownloadRequestData(
                            videoId = data.videoId,
                            isAudioOnly = false,
                            quality = null
                        ))
                    }
                )
            }

            // 履歴一覧のときのみ
            if (data.isHistory) {
                HistoryDeleteButton(
                    modifier = paddingModifier,
                    snackbarHostState = snackbarHostState,
                    onDelete = {
                        scope.launch {
                            viewModel.deleteHistoryFromVideoId(data.videoId)
                            onClose()
                        }
                    }
                )
            }

            // お気に入り削除ボタン
            data.folderId?.let { folderId ->
                FavoriteVideoDeleteButton(
                    modifier = paddingModifier,
                    snackbarHostState = snackbarHostState,
                    videoId = data.videoId,
                    folderId = folderId,
                    onDeleteClick = { videoId, folderId ->
                        viewModel.deleteFavoriteVideoItem(videoId, folderId)
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