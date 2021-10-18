package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.ui.component.HistoryVideoList
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
@ExperimentalMaterialApi
@Composable
fun HistoryScreen(
    mainViewModel: MainScreenViewModel,
    historyScreenViewModel: HistoryScreenViewModel = viewModel(),
    navController: NavHostController
) {
    val historyList = historyScreenViewModel.historyDBDataListFlow.collectAsState(initial = listOf())
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { ChocoBridgeBar(viewModel = mainViewModel, navHostController = navController) },
        content = {
            Column(Modifier.padding(it)) {
                // 消すボタン
                Button(
                    onClick = {
                        SnackbarComposeTool.showSnackbar(
                            scope = scope,
                            snackbarDuration = SnackbarDuration.Long,
                            snackbarHostState = scaffoldState.snackbarHostState,
                            snackbarMessage = "本当に削除しますか？",
                            actionLabel = "削除",
                            onActionPerformed = { historyScreenViewModel.deleteAllDB() }
                        )
                    },
                    content = { Text(text = "全件削除") }
                )
                // 一覧表示
                Divider()
                if (historyList.value.isNotEmpty()) {
                    HistoryVideoList(
                        historyDBEntityList = historyList.value,
                        onClick = { mainViewModel.loadWatchPage(it) }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "履歴はありません。これからに期待！")
                    }
                }
            }
        }
    )
}