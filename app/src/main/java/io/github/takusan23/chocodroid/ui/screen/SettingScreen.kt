package io.github.takusan23.chocodroid.ui.component

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
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import kotlinx.coroutines.launch

/**
 * 設定画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // DataStoreからFlowで受け取る
    val dataStore = context.dataStore
    val dataStoreFlow = context.dataStore.data.collectAsState(initial = null)

    M3Scaffold {
        LazyColumn(
            modifier = Modifier.padding(it),
            content = {
                // タイトル
                item {
                    LargeTopAppBar(
                        title = { Text(text = stringResource(id = R.string.setting)) }
                    )
                }
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
                        onClick = { }
                    )
                }
                item {
                    SettingItem(
                        title = stringResource(id = R.string.setting_license_title),
                        description = stringResource(id = R.string.setting_license_description),
                        icon = null,
                        onClick = { }
                    )
                }
            }
        )
    }
}