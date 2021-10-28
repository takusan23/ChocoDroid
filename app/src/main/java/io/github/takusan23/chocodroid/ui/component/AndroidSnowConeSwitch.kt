package io.github.takusan23.chocodroid.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Android 12 (SnowCone) スタイルのスイッチを作成する。
 *
 * 多分そのうち公式で出ると思うけど出るまで
 *
 * @param modifier Modifier
 * @param isEnable ON / OFF
 * @param onValueChange 値変更時に呼ばれる
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AndroidSnowConeSwitch(
    modifier: Modifier = Modifier,
    isEnable: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    val backgroundColor = animateColorAsState(targetValue = if (isEnable) androidx.compose.material3.MaterialTheme.colorScheme.primary else Color.DarkGray)
    Surface(
        modifier = modifier
            .height(30.dp)
            .width(60.dp),
        color = backgroundColor.value,
        shape = RoundedCornerShape(50),
        onClick = { onValueChange(!isEnable) }
    ) {
        BoxWithConstraints {
            val offsetXAnim = animateDpAsState(targetValue = if (isEnable) (maxWidth / 2) else 0.dp)
            val foregroundColor = animateColorAsState(targetValue = if (isEnable) MaterialTheme.colorScheme.background else Color.LightGray)
            Surface(
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxHeight()
                    .offset(offsetXAnim.value, 0.dp)
                    .aspectRatio(1f),
                color = foregroundColor.value,
                shape = RoundedCornerShape(50),
                content = { }
            )
        }
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun SwitchPreview() {
    val isEnable = remember { mutableStateOf(true) }
    AndroidSnowConeSwitch(
        isEnable = isEnable.value,
        onValueChange = { isEnable.value = it }
    )
}