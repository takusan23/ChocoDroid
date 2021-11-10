package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * インジケータの角が丸いタブレイアウト
 *
 * @param modifier Modifier
 * @param selectIndex 選択中タブ
 * @param tabs TabItemを入れる
 * */
@Composable
fun Material3TabLayout(
    modifier: Modifier,
    selectIndex: Int,
    tabs: @Composable () -> Unit,
) {
    TabRow(
        modifier = modifier,
        selectedTabIndex = selectIndex,
        contentColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.surface,
        indicator = { tabPositions ->
            // テキストの下に出るあの棒のやつ
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectIndex])
                    .height(3.dp)
                    .padding(start = 20.dp, end = 20.dp)
                    .background(LocalContentColor.current, RoundedCornerShape(100, 100, 0, 0))
            )
        },
        tabs = tabs
    )
}