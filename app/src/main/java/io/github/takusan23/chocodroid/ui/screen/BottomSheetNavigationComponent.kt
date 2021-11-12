package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.AddFavoriteFolder
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.ChocoDroidBottomSheetNavigationLinkList
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.QualityChangeScreen
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel
import kotlinx.coroutines.launch

/**
 * BottomSheetのナビゲーション。画面遷移
 *
 * @param mainScreenViewModel メイン画面ViewModel
 * @param bottomSheetNavHostController 画面遷移コントローラー
 * @param modalBottomSheetState ボトムシート制御用
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChocoDroidBottomSheetNavigation(
    mainScreenViewModel: MainScreenViewModel,
    bottomSheetNavHostController: NavHostController = rememberNavController(),
    modalBottomSheetState: ModalBottomSheetState,
) {
    val scope = rememberCoroutineScope()

    Surface {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 引き出す棒
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .width(100.dp)
                    .height(10.dp)
                    .background(color = Color.Gray, RoundedCornerShape(50))
            )
            // 画面遷移
            NavHost(navController = bottomSheetNavHostController, startDestination = ChocoDroidBottomSheetNavigationLinkList.QualityChange) {
                composable(ChocoDroidBottomSheetNavigationLinkList.QualityChange) {
                    QualityChangeScreen(
                        mainScreenViewModel = mainScreenViewModel,
                        onClose = { scope.launch { modalBottomSheetState.hide() } }
                    )
                }
                composable(ChocoDroidBottomSheetNavigationLinkList.AddFavoriteFolder) {
                    AddFavoriteFolder(onClose = { scope.launch { modalBottomSheetState.hide() } })
                }
            }
        }
    }
}