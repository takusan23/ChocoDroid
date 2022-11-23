package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackButtonSmallTopBar(
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    onBack: () -> Unit,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
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