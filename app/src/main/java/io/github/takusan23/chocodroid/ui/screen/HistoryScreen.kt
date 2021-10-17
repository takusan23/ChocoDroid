package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * 履歴画面
 *
 * @param viewModel メイン画面のViewModel
 * @param navController メイン画面のNavController
 * */
@ExperimentalMaterialApi
@Composable
fun HistoryScreen(viewModel: MainScreenViewModel, navController: NavHostController) {
    Scaffold(
        topBar = { ChocoBridgeBar(viewModel = viewModel, navHostController = navController) },
        content = {
            Text(text = "履歴画面")
        }
    )
}