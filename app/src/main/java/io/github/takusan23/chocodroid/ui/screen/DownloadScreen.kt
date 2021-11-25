package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
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
    // 動画一覧を取得する
    val videoList = downloadScreenVideModel.downloadContentFlow.collectAsState(initial = listOf())

    Scaffold(
        topBar = { ChocoBridgeBar(viewModel = mainScreenViewModel, navHostController = navController) },
        content = {
            Column(modifier = Modifier.padding(it)) {
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