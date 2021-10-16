package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * お気に入り画面
 * */
@ExperimentalMaterialApi
@Composable
fun FavouriteScreen(viewModel: MainScreenViewModel, navController: NavHostController) {
    Scaffold(
        topBar = {
            ChocoBridgeBar(viewModel = viewModel, navHostController = navController)
        },
        content = {
            Text(text = "お気に入り画面")
        }
    )

}