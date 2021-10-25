package io.github.takusan23.chocodroid.ui.screen.videodetail

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.RoundedIconButton
import io.github.takusan23.chocodroid.ui.component.RoundedImageButton
import io.github.takusan23.chocodroid.ui.screen.NavigationLinkList
import io.github.takusan23.internet.data.watchpage.WatchPageData

/**
 * 動画説明文。再生回数やタイトルなど
 *
 * @param watchPageData 視聴ページレスポンスデータ
 * @param onNavigation 今の所チャンネル画面に遷移する目的以外では使ってない
 * */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun VideoDetailDescriptionScreen(
    watchPageData: WatchPageData,
    onNavigation: (String) -> Unit,
) {
    val videoDetails = watchPageData.watchPageResponseJSONData.videoDetails
    val iconUrl = watchPageData.watchPageInitialJSONData.contents.twoColumnWatchNextResults.results.results.contents[1].videoSecondaryInfoRenderer?.owner?.videoOwnerRenderer?.thumbnail?.thumbnails?.last()?.url

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(
            modifier = Modifier.padding(10.dp),
            fontSize = 20.sp,
            text = videoDetails.title,
        )

        Row {
            RoundedIconButton(
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f),
                mainText = videoDetails.viewCount,
                subText = stringResource(id = R.string.watch_count),
                iconPainter = painterResource(id = R.drawable.ic_outline_play_arrow_24),
                onClick = { }
            )
            RoundedIconButton(
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f),
                mainText = watchPageData.watchPageResponseJSONData.microformat.playerMicroformatRenderer.publishDate,
                subText = stringResource(id = R.string.publish_date),
                iconPainter = painterResource(id = R.drawable.ic_outline_today_24),
                onClick = { }
            )
        }

        RoundedImageButton(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            mainText = videoDetails.author,
            subText = stringResource(id = R.string.publish_name),
            iconPainter = rememberImagePainter(
                data = iconUrl,
                builder = { crossfade(true) }
            ),
            onClick = { onNavigation(NavigationLinkList.getChannelScreenLink(watchPageData.watchPageResponseJSONData.videoDetails.channelId)) }
        )

        Divider()
        Text(
            modifier = Modifier.padding(10.dp),
            fontSize = 15.sp,
            text = watchPageData.watchPageResponseJSONData.videoDetails.shortDescription,
        )
    }
}