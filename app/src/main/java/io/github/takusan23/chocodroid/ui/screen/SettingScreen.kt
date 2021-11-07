package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.edit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.chocodroid.ui.screen.setting.AboutSettingScreen
import io.github.takusan23.chocodroid.ui.screen.setting.FirstSettingScreen
import io.github.takusan23.chocodroid.ui.screen.setting.LicenseSettingScreen
import io.github.takusan23.chocodroid.ui.screen.setting.SettingNavigationLinkList
import kotlinx.coroutines.launch

/**
 * 設定画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen() {
    val navController = rememberNavController()

    M3Scaffold(
        topBar = { LargeTopAppBar(title = { Text(text = stringResource(id = R.string.setting)) }) },
        content = {
            Box(modifier = Modifier.padding(it)) {
                NavHost(navController = navController, startDestination = SettingNavigationLinkList.FirstScreen) {
                    composable(SettingNavigationLinkList.FirstScreen) {
                        FirstSettingScreen(onNavigate = { navController.navigate(it) })
                    }
                    composable(SettingNavigationLinkList.AboutAppScreen) {
                        AboutSettingScreen()
                    }
                    composable(SettingNavigationLinkList.LicenseScreen) {
                        LicenseSettingScreen()
                    }
                }
            }
        }
    )
}