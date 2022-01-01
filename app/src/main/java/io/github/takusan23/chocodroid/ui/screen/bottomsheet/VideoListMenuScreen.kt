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
import io.github.takusan23.chocodroid.service.DownloadContentBackgroundPlayerService
import io.github.takusan23.chocodroid.ui.component.*
import io.github.takusan23.chocodroid.viewmodel.VideoListMenuScreenViewModel
import kotlinx.coroutines.launch

/**
 * 動画一覧から開くメニュー。いろいろボタンがあるVer
 *
 * @param viewModel Composeにデータベース処理とか書いていいの？よくわからんからViewModelに書いてる
 * @param initData メニューに渡すデータクラス
 * @param snackbarHostState Snackbarだすやつ
 * @param onClose 閉じてほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListMenuScreen(
    viewModel: VideoListMenuScreenViewModel = viewModel(),
    initData: VideoListMenuScreenInitData,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onClose: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val paddingModifier = Modifier.padding(start = 5.dp, top = 5.dp, end = 5.dp)

    VideoListMenuScreen(
        videoTitle = initData.videoTitle,
        snackbarHostState = snackbarHostState,
        content = {

            if (initData.isDownloadContent) {
                // ダウンロードのときのみ
                ExportDeviceMediaFolderButton(
                    modifier = paddingModifier,
                    onClick = {
                        scope.launch {
                            viewModel.copyFileToVideoOrMusicFolder(initData.videoId)
                            Toast.makeText(context, context.getString(R.string.copy_successful), Toast.LENGTH_SHORT).show()
                            onClose()
                        }
                    }
                )
                DownloadBackgroundPlayerButton(
                    modifier = paddingModifier,
                    onClick = {
                        DownloadContentBackgroundPlayerService.startService(
                            context = context,
                            startVideoId = initData.videoId
                        )
                    }
                )
                DownloadContentDeleteButton(
                    modifier = paddingModifier,
                    snackbarHostState = snackbarHostState,
                    onDeleteClick = {
                        viewModel.deleteDownloadContent(initData.videoId)
                        onClose()
                    }
                )
            } else {
                // ダウンロードサービス起動
                DownloadButton(
                    modifier = paddingModifier,
                    onClick = {
                        ContentDownloadService.startDownloadService(context, DownloadRequestData(
                            videoId = initData.videoId,
                            isAudioOnly = false,
                            quality = null
                        ))
                    }
                )
            }

            // 履歴一覧のときのみ
            if (initData.isHistory) {
                HistoryDeleteButton(
                    modifier = paddingModifier,
                    snackbarHostState = snackbarHostState,
                    onDelete = {
                        scope.launch {
                            viewModel.deleteHistoryFromVideoId(initData.videoId)
                            onClose()
                        }
                    }
                )
            }

            // お気に入り削除ボタン
            initData.folderId?.let { folderId ->
                FavoriteVideoDeleteButton(
                    modifier = paddingModifier,
                    snackbarHostState = snackbarHostState,
                    videoId = initData.videoId,
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

/**
 * [VideoListMenuScreen]を表示する際に渡すデータ
 *
 * @param videoId 動画ID
 * @param videoTitle 動画タイトル
 * @param folderId お気に入りフォルダ内の動画の場合はフォルダIDを入れる
 * @param isDownloadContent ダウンロード済みコンテンツの場合はtrue
 * @param isHistory 履歴一覧から表示の場合はtrue。履歴削除ボタンを表示します
 */
data class VideoListMenuScreenInitData(
    val videoId: String,
    val videoTitle: String,
    val folderId: Int? = null,
    val isDownloadContent: Boolean = false,
    val isHistory: Boolean = false,
) : BottomSheetInitData(BottomSheetScreenList.VideoListMenu)