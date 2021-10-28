package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
 * こっちはIconです。画像は表示できません。アイコンのみです
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
fun RoundedIconButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(0.5f),
    shape: Shape = RoundedCornerShape(30.dp),
    mainText: String,
    subText: String,
    iconPainter: Painter,
    onClick: () -> Unit,
) = RoundedButton(
    modifier = modifier,
    mainText = mainText,
    subText = subText,
    backgroundColor = backgroundColor,
    shape = shape,
    icon = {
        Icon(
            painter = iconPainter,
            contentDescription = null,
            modifier = Modifier.padding(start = 10.dp)
        )
    },
    onClick = onClick
)


/**
 * Android 12のクイック設定みたいなクソデカボタン
 *
 * こっちはIconではなくImageです。画像の表示ができます
 *
 * @param modifier Modifier
 * @param shape どれぐらい丸くするか
 * @param mainText メインテキスト
 * @param subText メインテキストのしたに置くサブテキスト
 * @param iconPainter ボタンのアイコン
 * @param backgroundColor ボタンの背景
 * @param iconRoundedPercent アイコンの角丸レベル
 * @param onClick 押したとき
 * */
@ExperimentalMaterialApi
@Composable
fun RoundedImageButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(0.5f),
    shape: Shape = RoundedCornerShape(30.dp),
    mainText: String,
    subText: String,
    iconPainter: Painter,
    iconRoundedPercent: Int = 50,
    onClick: () -> Unit,
) = RoundedButton(
    modifier = modifier,
    mainText = mainText,
    subText = subText,
    backgroundColor = backgroundColor,
    shape = shape,
    icon = {
        // 画像をを丸くするのに丸くしたSurfaceの中でアイコンを追加したほうがいいってどっかで読んだような
        Surface(
            modifier = Modifier.padding(start = 10.dp),
            shape = RoundedCornerShape(iconRoundedPercent)
        ) {
            Image(
                painter = iconPainter,
                modifier = Modifier
                    .size(30.dp)
                    .fillMaxSize(),
                contentDescription = null,
            )
        }
    },
    onClick = onClick
)

/**
 * Icon部分を変更可能にしたバージョン。
 * */
@ExperimentalMaterialApi
@Composable
private fun RoundedButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(0.5f),
    shape: Shape = RoundedCornerShape(30.dp),
    mainText: String,
    subText: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = shape,
        onClick = onClick,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                Text(
                    fontSize = 18.sp,
                    maxLines = 1,
                    text = mainText
                )
                Text(
                    text = subText,
                    maxLines = 1,
                    fontSize = 14.sp,
                )
            }
        }
    }
}