package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.ChocoDroidBottomSheetNavigationLinkList
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.QualityChangeScreen
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * BottomSheetのナビゲーション。画面遷移
 *
 * @param mainScreenViewModel メイン画面ViewModel
 * @param navHostController 画面遷移コントローラー
 * */
@Composable
fun ChocoDroidBottomSheetNavigation(
    mainScreenViewModel: MainScreenViewModel,
    navHostController: NavHostController = rememberNavController(),
) {
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
            NavHost(navController = navHostController, startDestination = ChocoDroidBottomSheetNavigationLinkList.QualityChange) {
                composable(ChocoDroidBottomSheetNavigationLinkList.QualityChange) {
                    QualityChangeScreen(mainScreenViewModel = mainScreenViewModel)
                }
            }
        }
    }
}