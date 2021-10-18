package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * お気に入り画面
 *
 * @param viewModel メイン画面のViewModel
 * @param navController メイン画面のNavController
 * */
@ExperimentalMaterialApi
@Composable
fun FavouriteScreen(viewModel: MainScreenViewModel, navController: NavHostController) {
    Scaffold(
        topBar = { ChocoBridgeBar(viewModel = viewModel, navHostController = navController) },
        content = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "お気に入り画面")
            }
        }
    )

}