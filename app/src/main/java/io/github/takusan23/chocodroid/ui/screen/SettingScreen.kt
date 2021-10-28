package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import kotlinx.coroutines.launch

/**
 * 設定画面
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // DataStoreからFlowで受け取る
    val dataStore = context.dataStore
    val dataStoreFlow = context.dataStore.data.collectAsState(initial = null)

    LazyColumn(
        content = {
            // タイトル
            item {
                SettingTitle(
                    stringResource(id = R.string.setting),
                    painterResource(id = R.drawable.ic_outline_settings_24)
                )
            }
            // 設定画面
            item {
                SettingSwitchItem(
                    title = "Material You(動的テーマ、ダイナミックカラー)を有効にする",
                    description = "Android 12以降のみ利用できます",
                    icon = painterResource(id = R.drawable.ic_outline_color_lens_24),
                    isEnable = dataStoreFlow.value?.get(SettingKeyObject.ENABLE_DYNAMIC_THEME) ?: false,
                    onCheckedChange = { isEnable -> scope.launch { dataStore.edit { it[SettingKeyObject.ENABLE_DYNAMIC_THEME] = isEnable } } }
                )
            }
            item {
                SettingItem(
                    title = "このアプリについて",
                    description = "自己紹介",
                    icon = painterResource(id = R.drawable.chocodroid_white_android),
                    onClick = { }
                )
            }
            item {
                SettingItem(
                    title = "ライセンス",
                    description = "thx!",
                    icon = null,
                    onClick = { }
                )
            }
        }
    )
}