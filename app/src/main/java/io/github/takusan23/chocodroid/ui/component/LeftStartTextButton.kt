package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * 左揃えなTextButton
 *
 * なぜか内部で中央揃えにしているので修正
 *
 * @param modifier Modifier
 * @param onClick 押したとき
 * @param content ボタンの中身
 * */
@Composable
fun LeftStartTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        content = { Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, content = content) }
    )
}