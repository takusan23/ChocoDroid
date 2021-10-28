package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.takusan23.chocodroid.R

/**
 * 検索のソートのやつ。ToggleButtonGroupみたいなやつ
 *
 * @param selectedItemPos 選択中の位置
 * @param onClick 押したときに呼ばれる。引数は数字
 * */
@Composable
fun SearchSortToggleButton(
    modifier: Modifier = Modifier,
    selectedItemPos: Int = 1,
    onClick: (Int) -> Unit,
) {
    val borderColor = LocalTextStyle.current.color.copy(0.5f)
    val selectedColor = androidx.compose.material3.MaterialTheme.colorScheme.primary

    // アイコンリスト
    val iconList = listOf(
        painterResource(id = R.drawable.ic_outline_auto_graph_24),
        painterResource(id = R.drawable.ic_outline_today_24),
        painterResource(id = R.drawable.ic_outline_play_arrow_24),
        painterResource(id = R.drawable.ic_outline_thumbs_up_down_24),
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(5.dp),
        color = Color.Transparent,
        border = BorderStroke(width = 1.dp, color = borderColor)
    ) {
        Row {
            iconList.forEachIndexed { index, painter ->
                Surface(
                    shape = RoundedCornerShape(5.dp),
                    color = Color.Transparent,
                    contentColor = if (index == selectedItemPos) selectedColor else borderColor,
                    border = if (index == selectedItemPos) BorderStroke(width = 1.dp, color = selectedColor) else null
                ) {
                    IconButton(onClick = { onClick(index) }) {
                        Icon(modifier = Modifier.padding(5.dp), painter = painter, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchSortToggleButtonPrev() {
    val selectedItemPos = remember { mutableStateOf(0) }
    SearchSortToggleButton(
        selectedItemPos = selectedItemPos.value,
        onClick = { selectedItemPos.value = it }
    )
}