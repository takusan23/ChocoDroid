package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.takusan23.chocodroid.setting.dataStore
import kotlinx.coroutines.launch

/**
 * 設定画面のタイトル部分
 *
 * @param icon アイコンを表示させる場合は
 * @param title タイトル
 * */
@Composable
fun SettingTitle(
    title: String,
    icon: Painter? = null,
) {
    Column(modifier = Modifier.padding(start = 10.dp, top = 50.dp, end = 10.dp, bottom = 10.dp)) {
        if (icon != null) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(bottom = 10.dp)
            )
        }
        Text(
            text = title,
            fontSize = 40.sp
        )
        Divider()
    }
}

/**
 * 設定の各項目
 *
 * @param title タイトル
 * @param description 説明
 * @param icon アイコン。省略時はスペースだけ開けます
 * @param onClick 押したとき
 * */
@ExperimentalMaterialApi
@Composable
fun SettingItem(
    title: String,
    description: String,
    icon: Painter?,
    onClick: () -> Unit,
) {
    Surface(onClick = onClick) {
        CommonSettingItem(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            title = title,
            description = description,
            icon = icon
        )
    }
}

/**
 * 設定の各項目。On/Off版
 *
 * @param title タイトル
 * @param description 説明
 * @param icon アイコン。省略時はスペースだけ開けます
 * @param isEnable 有効かどうか
 * @param onCheckedChange 有効無効切り替わったとき
 * */
@ExperimentalMaterialApi
@Composable
fun SettingSwitchItem(
    title: String,
    description: String,
    icon: Painter,
    isEnable: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(onClick = { onCheckedChange(!isEnable) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CommonSettingItem(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f),
                title = title,
                description = description,
                icon = icon
            )
            AndroidSnowConeSwitch(
                modifier = Modifier.padding(10.dp),
                isEnable = isEnable,
                onValueChange = onCheckedChange
            )
        }
    }
}

/**
 * 設定の各項目の共通部分を抜き出した
 *
 * @param modifier Modifier
 * @param title タイトル
 * @param description 説明
 * @param icon アイコン。省略時はスペースだけ開けます
 * */
@Composable
private fun CommonSettingItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: Painter?,
) {
    Row(
        modifier = modifier.padding(start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Image(
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 10.dp),
                painter = icon,
                contentDescription = null
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 10.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                modifier = Modifier.padding(bottom = 5.dp),
                fontSize = 18.sp
            )
            Text(text = description)
        }
    }
}