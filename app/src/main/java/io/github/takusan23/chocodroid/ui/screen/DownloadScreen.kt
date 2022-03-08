package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.service.DownloadContentBackgroundPlayerService
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.ui.component.DownloadContentBackgroundPlayButton
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.component.VideoList
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.BottomSheetInitData
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.VideoListMenuScreenInitData
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
    onBottomSheetNavigate: (BottomSheetInitData) -> Unit,
) {
    val context = LocalContext.current

    // 動画一覧を取得する
    val videoList = downloadScreenVideModel.downloadContentFlow.collectAsState(initial = listOf())

    M3Scaffold(
        topBar = {
            ChocoBridgeBar(
                onClick = { navController.navigate(NavigationLinkList.getChocoDroidBridgeSearchScreen()) },
                onSettingClick = { navController.navigate(NavigationLinkList.SettingScreen) }
            )
        },
        content = {
            Column(modifier = Modifier.padding(it)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    content = {
                        DownloadContentBackgroundPlayButton(modifier = Modifier.padding(end = 10.dp)) {
                            DownloadContentBackgroundPlayerService.startService(context)
                        }
                    }
                )
                if (videoList.value.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                            .fillMaxHeight(),
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        content = {
                            VideoList(
                                videoList = videoList.value,
                                onClick = { mainScreenViewModel.loadWatchPageFromLocal(it) },
                                onMenuClick = { videoData ->
                                    onBottomSheetNavigate(VideoListMenuScreenInitData(
                                        commonVideoData = videoData,
                                        isDownloadContent = true
                                    ))
                                }
                            )
                        }
                    )
                }
            }
        }
    )
}