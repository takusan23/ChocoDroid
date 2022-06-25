package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * バッファーを表示できる シークバー
 *
 * @param modifier [Modifier]
 * @param progressBuffered バッファの進捗
 * @param onValueChange 再生位置
 * @param onValueChangeFinished シークバー操作時に呼ばれる
 * @param progressFloat シークバーの操作終了時に呼ばれる
 */
@Composable
fun BufferSeekbar(
    modifier: Modifier = Modifier,
    progressBuffered: Float,
    progressFloat: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // バッファどこまで読み込んだかを表示するプログレスバー
        // View時代にはSecondaryProgressみたいなのがあったけどComposeには無い？
        // 角を丸くするためにSurfaceでくくる
        Surface(
            modifier = Modifier.padding(
                // つまみの大きさを引く
                start = 10.dp, end = 10.dp
            ),
            color = Color.Transparent,
            shape = RoundedCornerShape(10.dp)
        ) {
            // シークバーのバックグラウンド
            // Sliderのバックグラウンドは透明化して、バックグラウンドだけ自前で描画する
            LinearProgressIndicator(
                progress = 1f,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                trackColor = Color.Transparent
            )
            // 進捗表示
            LinearProgressIndicator(
                progress = progressBuffered,
                color = MaterialTheme.colorScheme.surfaceVariant,
                trackColor = Color.Transparent
            )
        }
        // シークバー
        // シークバーの背景は自前で描画するため透明、再生位置とつまみを使う
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = progressFloat,
            colors = SliderDefaults.colors(
                inactiveTrackColor = Color.Transparent
            ),
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished
        )
    }

}