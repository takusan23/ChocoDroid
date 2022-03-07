package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 設定の各項目
 *
 * @param title タイトル
 * @param description 説明
 * @param icon アイコン。省略時はスペースだけ開けます
 * @param onClick 押したとき
 * */
@Composable
fun SettingItem(
    title: String,
    description: String,
    icon: Painter?,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            ),
        color = Color.Transparent,
    ) {
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
@Composable
fun SettingSwitchItem(
    title: String,
    description: String,
    icon: Painter,
    isEnable: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = { onCheckedChange(!isEnable) }
            ),
        color = Color.Transparent,
    ) {
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
                    .size(40.dp)
                    .padding(end = 10.dp),
                painter = icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(40.dp)
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