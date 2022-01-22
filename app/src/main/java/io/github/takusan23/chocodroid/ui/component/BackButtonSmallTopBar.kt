package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import io.github.takusan23.chocodroid.R

/**
 * 戻るボタン付きのSmallTopBar
 *
 * @param title タイトル
 * @param onBack 戻るボタン
 * @param actions 左のボタン
 * */
@Composable
fun BackButtonSmallTopBar(
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    onBack: () -> Unit,
) {
    SmallTopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent
        ),
        title = title,
        actions = actions,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(painter = painterResource(id = R.drawable.ic_outline_arrow_back_24), contentDescription = null)
            }
        }
    )
}