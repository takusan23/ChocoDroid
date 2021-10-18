package io.github.takusan23.chocodroid.ui.component.videodetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.RoundedButton
import io.github.takusan23.htmlparse.data.watchpage.WatchPageData

/**
 * 動画説明文。再生回数やタイトルなど
 *
 * @param watchPageData 視聴ページレスポンスデータ
 * */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun VideoDetailDescriptionScreen(watchPageData: WatchPageData) {
    val videoDetails = watchPageData.watchPageJSONResponseData.videoDetails
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(
            modifier = Modifier.padding(10.dp),
            fontSize = 20.sp,
            text = videoDetails.title,
        )

        Row {
            RoundedButton(
                modifier = Modifier.weight(1f),
                mainText = videoDetails.viewCount,
                subText = "再生回数",
                iconPainter = painterResource(id = R.drawable.ic_outline_play_arrow_24),
                onClick = { }
            )
            RoundedButton(
                modifier = Modifier.weight(1f),
                mainText = watchPageData.watchPageJSONResponseData.microformat.playerMicroformatRenderer.publishDate,
                subText = "投稿日時",
                iconPainter = painterResource(id = R.drawable.ic_outline_today_24),
                onClick = { }
            )
        }

        RoundedButton(
            modifier = Modifier.fillMaxWidth(),
            mainText = videoDetails.author,
            subText = "投稿者",
            iconPainter = painterResource(id = R.drawable.chocodroid_white_android),
            onClick = { }
        )

        Divider()
        Text(
            modifier = Modifier.padding(10.dp),
            fontSize = 15.sp,
            text = watchPageData.watchPageJSONResponseData.videoDetails.shortDescription,
        )
    }
}