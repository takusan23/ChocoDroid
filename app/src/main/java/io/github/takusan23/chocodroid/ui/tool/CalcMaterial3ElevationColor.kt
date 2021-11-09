package io.github.takusan23.chocodroid.ui.tool

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import kotlin.math.ln

/**
 * Material3で追加された色の明るさ、暗さを設定するtonalElevationの計算をする関数。
 *
 * 本家はinternalアクセス修飾子のせいで使えないのでコピーしてきた
 *
 * 本家BottomNavigationの色を取得する場合は[color]に[ColorScheme.surface]を、[elevation]に[3.dp]入れてください。
 *
 * @param elevation 明るさ、暗さの設定。詳しくはMaterial3のSurfaceのドキュメント見て
 * @param colorScheme [MaterialTheme.colorScheme]
 * @param color [ColorScheme.surface]を入れればいいと思う
 * */
@Composable
fun calcM3ElevationColor(colorScheme: ColorScheme, color: Color, elevation: Dp): Color {
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return colorScheme.primary.copy(alpha = alpha).compositeOver(color)
}