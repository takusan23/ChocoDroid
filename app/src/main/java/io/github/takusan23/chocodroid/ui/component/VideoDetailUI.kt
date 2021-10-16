package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.htmlparse.data.watchpage.WatchPageData

/**
 * 動画説明部分のUI
 *
 * @param watchPageData 視聴ページレスポンスデータ
 * */
@ExperimentalMaterialApi
@Composable
fun VideoDetailUI(watchPageData: WatchPageData) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            NavigationRail(
                elevation = 0.dp,
                content = {
                    NavigationRailItem(
                        selected = false,
                        icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_info_24), contentDescription = null) },
                        label = { Text(text = "動画情報") },
                        onClick = { }
                    )
                    NavigationRailItem(
                        selected = false,
                        icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_more_vert_24), contentDescription = null) },
                        label = { Text(text = "メニュー") },
                        onClick = { }
                    )
                }
            )
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
                elevation = 10.dp
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

    }
}