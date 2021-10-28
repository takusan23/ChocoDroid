package io.github.takusan23.chocodroid.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import io.github.takusan23.chocodroid.R

/**
 * リピートボタン
 *
 * 外でも使うかもしれんし切り出した
 *
 * @param isEnableRepeat リピートONならtrue
 * @param onRepeatChange 切り替えたら呼ばれる
 * */
@Composable
fun RepeatButton(
    modifier: Modifier = Modifier,
    isEnableRepeat: Boolean,
    onRepeatChange: (Boolean) -> Unit,
) {
    IconToggleButton(
        modifier = modifier,
        checked = isEnableRepeat,
        onCheckedChange = onRepeatChange,
        content = {
            val iconId = if (isEnableRepeat) R.drawable.ic_baseline_repeat_one_24 else R.drawable.ic_outline_repeat_24
            Icon(painter = painterResource(id = iconId), contentDescription = null)
        }
    )
}