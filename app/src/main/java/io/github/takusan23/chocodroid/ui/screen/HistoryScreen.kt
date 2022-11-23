package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.ui.component.HistoryAllDeleteButton
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.component.VideoList
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.BottomSheetInitData
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.VideoListMenuScreenInitData
import io.github.takusan23.chocodroid.ui.theme.SurfaceElevations
import io.github.takusan23.chocodroid.viewmodel.HistoryScreenViewModel
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * 履歴画面
 *
 * @param mainViewModel メイン画面のViewModel
 * @param historyScreenViewModel 履歴画面のViewModel
 * @param navController メイン画面のNavController
 * @param onBottomSheetNavigate BottomSheet画面遷移
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    mainViewModel: MainScreenViewModel,
    historyScreenViewModel: HistoryScreenViewModel = viewModel(),
    navController: NavHostController,
    onBottomSheetNavigate: (BottomSheetInitData) -> Unit,
) {
    val historyList = historyScreenViewModel.historyDBDataListFlow.collectAsState(initial = listOf())
    val snackbarHostState = remember { SnackbarHostState() }

    M3Scaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            ChocoBridgeBar(
                onClick = { navController.navigate(NavigationLinkList.getChocoDroidBridgeSearchScreen()) },
                onSettingClick = { navController.navigate(NavigationLinkList.SettingScreen) }
            )
        }
    ) {
        Column {
            // 削除ボタン
            HistoryAllDeleteButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 10.dp),
                snackbarHostState = snackbarHostState,
                onDelete = { historyScreenViewModel.deleteAllDB() }
            )
            // 一覧表示
            if (historyList.value.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(SurfaceElevations.VideoListBackgroundElevation),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    VideoList(
                        videoList = historyList.value,
                        onClick = { mainViewModel.loadWatchPage(it) },
                        onMenuClick = {
                            onBottomSheetNavigate(VideoListMenuScreenInitData(
                                commonVideoData = it,
                                isHistory = true
                            ))
                        }
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text(text = stringResource(id = R.string.history_empty)) }
            }
        }
    }
}