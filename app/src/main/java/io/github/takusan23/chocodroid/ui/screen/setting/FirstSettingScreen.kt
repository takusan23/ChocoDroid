package io.github.takusan23.chocodroid.ui.screen.setting

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.edit
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.component.SettingItem
import io.github.takusan23.chocodroid.ui.component.SettingSwitchItem
import kotlinx.coroutines.launch

/**
 * 設定画面で一番最初に表示する画面
 *
 * @param onNavigate 画面遷移先する際に呼ばれる。引数はnavigate()に入れて
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstSettingScreen(
    onNavigate: (String) -> Unit,
) {
    // DataStoreからFlowで受け取る
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = context.dataStore
    val dataStoreFlow = context.dataStore.data.collectAsState(initial = null)

    M3Scaffold(
        topBar = { LargeTopAppBar(title = { Text(text = stringResource(id = R.string.setting)) }) },
        content = {
            LazyColumn(
                content = {
                    // 設定画面
                    item {
                        SettingSwitchItem(
                            title = stringResource(id = R.string.setting_enable_dynamic_color_title),
                            description = stringResource(id = R.string.setting_enable_dynamic_color_description),
                            icon = painterResource(id = R.drawable.ic_outline_color_lens_24),
                            isEnable = dataStoreFlow.value?.get(SettingKeyObject.ENABLE_DYNAMIC_THEME) ?: false,
                            onCheckedChange = { isEnable -> scope.launch { dataStore.edit { it[SettingKeyObject.ENABLE_DYNAMIC_THEME] = isEnable } } }
                        )
                    }
                    item {
                        SettingItem(
                            title = stringResource(id = R.string.setting_kono_app_title),
                            description = stringResource(id = R.string.setting_kono_app_description),
                            icon = painterResource(id = R.drawable.chocodroid_white_android),
                            onClick = { onNavigate(SettingNavigationLinkList.AboutAppScreen) }
                        )
                    }
                    item {
                        SettingItem(
                            title = stringResource(id = R.string.setting_license_title),
                            description = stringResource(id = R.string.setting_license_description),
                            icon = null,
                            onClick = { onNavigate(SettingNavigationLinkList.LicenseScreen) }
                        )
                    }
                }
            )
        },
    )
}