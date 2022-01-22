package io.github.takusan23.chocodroid.ui.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.ui.screen.setting.AboutSettingScreen
import io.github.takusan23.chocodroid.ui.screen.setting.LicenseSettingScreen
import io.github.takusan23.chocodroid.ui.screen.setting.MasterSettingScreen
import io.github.takusan23.chocodroid.ui.screen.setting.SettingNavigationLinkList

/**
 * 設定画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = SettingNavigationLinkList.FirstScreen) {
        // 設定一覧
        composable(SettingNavigationLinkList.FirstScreen) {
            MasterSettingScreen(onNavigate = { navController.navigate(it) })
        }
        // このアプリについて
        composable(SettingNavigationLinkList.AboutAppScreen) {
            AboutSettingScreen(onBack = { navController.popBackStack() })
        }
        // ライセンス画面
        composable(SettingNavigationLinkList.LicenseScreen) {
            LicenseSettingScreen(onBack = { navController.popBackStack() })
        }
    }

}