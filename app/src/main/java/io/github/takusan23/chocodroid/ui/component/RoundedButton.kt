package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Android 12のクイック設定みたいなクソデカボタン
 *
 * @param modifier Modifier
 * @param shape どれぐらい丸くするか
 * @param mainText メインテキスト
 * @param subText メインテキストのしたに置くサブテキスト
 * @param iconPainter ボタンのアイコン
 * @param backgroundColor ボタンの背景
 * @param onClick 押したとき
 * */
@ExperimentalMaterialApi
@Composable
fun RoundedButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primary.copy(0.5f),
    shape: Shape = RoundedCornerShape(30.dp),
    mainText: String,
    subText: String,
    iconPainter: Painter,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.padding(10.dp),
        color = backgroundColor,
        shape = shape,
        onClick = onClick,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                modifier = Modifier.padding(10.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                Text(
                    fontSize = 18.sp,
                    text = mainText
                )
                Text(
                    text = subText,
                    fontSize = 14.sp,
                )
            }
        }
    }

}