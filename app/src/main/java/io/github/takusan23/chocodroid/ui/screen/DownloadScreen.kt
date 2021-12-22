package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.service.DownloadContentBackgroundPlayerService
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.ui.component.DownloadContentBackgroundPlayIconButton
import io.github.takusan23.chocodroid.ui.component.VideoList
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.ChocoDroidBottomSheetNavigationLinkList
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.VideoListMenuData
import io.github.takusan23.chocodroid.viewmodel.DownloadScreenVideModel
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * ダウンロード済み動画画面
 *
 * @param mainScreenViewModel メイン画面のViewModel
 * @param navController メイン画面のNavController
 * @param downloadScreenVideModel ダウンロード画面のViewModel
 * @param onBottomSheetNavigate BottomSheetの切り替えをしてほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    mainScreenViewModel: MainScreenViewModel,
    navController: NavHostController,
    downloadScreenVideModel: DownloadScreenVideModel,
    onBottomSheetNavigate: (String) -> Unit,
) {
    val context = LocalContext.current

    // 動画一覧を取得する
    val videoList = downloadScreenVideModel.downloadContentFlow.collectAsState(initial = listOf())

    Scaffold(
        topBar = { ChocoBridgeBar(viewModel = mainScreenViewModel, navHostController = navController) },
        content = {
            Column(modifier = Modifier.padding(it)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    DownloadContentBackgroundPlayIconButton(modifier = Modifier.padding(end = 10.dp)) {
                        DownloadContentBackgroundPlayerService.startService(context)
                    }
                }
                Divider()
                VideoList(
                    videoList = videoList.value,
                    onClick = { mainScreenViewModel.loadWatchPageFromLocal(it) },
                    onMenuClick = { videoData ->
                        onBottomSheetNavigate(ChocoDroidBottomSheetNavigationLinkList.getVideoListMenu(VideoListMenuData(
                            videoData.videoId,
                            videoData.videoTitle,
                            isDownloadContent = true
                        )))
                    }
                )
            }
        }
    )
}