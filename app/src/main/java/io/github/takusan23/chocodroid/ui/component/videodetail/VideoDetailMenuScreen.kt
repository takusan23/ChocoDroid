package io.github.takusan23.chocodroid.ui.component.videodetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.internet.data.watchpage.WatchPageData

/**
 * メニュー画面
 * */
@Composable
fun VideoDetailMenuScreen(watchPageData: WatchPageData) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(
            modifier = Modifier.padding(10.dp),
            fontSize = 20.sp,
            text = stringResource(id = R.string.video_menu),
        )
    }
}