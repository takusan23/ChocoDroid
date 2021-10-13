package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.htmlparse.html.data.WatchPageData

/**
 * 動画説明部分のUI
 *
 * @param watchPageData 視聴ページレスポンスデータ
 * */
@Composable
fun VideoDetailUI(watchPageData: WatchPageData) {
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(
                modifier = Modifier.padding(5.dp),
                fontSize = 20.sp,
                text = watchPageData.watchPageJSONResponseData.videoDetails.title,
            )
            Text(
                modifier = Modifier.padding(5.dp),
                fontSize = 15.sp,
                text = watchPageData.watchPageJSONResponseData.videoDetails.shortDescription,
            )
        }
    }
}