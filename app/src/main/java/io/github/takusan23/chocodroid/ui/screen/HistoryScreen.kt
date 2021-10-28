package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.component.VideoList
import io.github.takusan23.chocodroid.ui.component.tool.SnackbarComposeTool
import io.github.takusan23.chocodroid.viewmodel.HistoryScreenViewModel
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * 履歴画面
 *
 * @param mainViewModel メイン画面のViewModel
 * @param historyScreenViewModel 履歴画面のViewModel
 * @param navController メイン画面のNavController
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    mainViewModel: MainScreenViewModel,
    historyScreenViewModel: HistoryScreenViewModel = viewModel(),
    navController: NavHostController,
) {
    val historyList = historyScreenViewModel.historyDBDataListFlow.collectAsState(initial = listOf())
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    M3Scaffold(
        scaffoldState = scaffoldState,
        snackbarHostState = snackbarHostState,
        topBar = { ChocoBridgeBar(viewModel = mainViewModel, navHostController = navController) },
        content = {
            Column(modifier = Modifier.padding(it)) {
                // 消すボタン
                Button(
                    modifier = Modifier
                        .align(alignment = Alignment.End)
                        .padding(end = 10.dp),
                    onClick = {
                        SnackbarComposeTool.showSnackbar(
                            scope = scope,
                            snackbarDuration = SnackbarDuration.Long,
                            snackbarHostState = snackbarHostState,
                            snackbarMessage = context.getString(R.string.delete_message),
                            actionLabel = context.getString(R.string.delete),
                            onActionPerformed = { historyScreenViewModel.deleteAllDB() }
                        )
                    },
                    content = { Text(text = stringResource(id = R.string.delete_all)) },
                )
                // 一覧表示
                Divider()
                if (historyList.value.isNotEmpty()) {
                    VideoList(
                        isSwipeEnabled = false,
                        videoList = historyList.value,
                        onClick = { mainViewModel.loadWatchPage(it) }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(id = R.string.history_empty))
                    }
                }
            }
        }
    )
}