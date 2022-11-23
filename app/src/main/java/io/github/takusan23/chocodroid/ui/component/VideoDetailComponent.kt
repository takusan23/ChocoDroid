package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.screen.NavigationLinkList
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.*
import io.github.takusan23.internet.data.CommonVideoData
import io.github.takusan23.internet.data.watchpage.WatchPageData

/**
 * 動画情報カード
 *
 * @param modifier [Modifier]
 * @param isExpanded 動画説明文展開中ならtrue
 * @param onNavigate 画面遷移してほしいときに呼ばれます
 * @param onOpenClick 動画説明文展開してほしいときに呼ばれる
 * @param watchPageData 動画情報
 * */
@Composable
fun VideoDetailInfoCard(
    modifier: Modifier = Modifier,
    watchPageData: WatchPageData,
    onNavigate: (String) -> Unit,
    isExpanded: Boolean,
    onOpenClick: (Boolean) -> Unit,
) {
    val videoDetails = watchPageData.watchPageResponseJSONData.videoDetails
    val publishDate = watchPageData.watchPageResponseJSONData.microformat.playerMicroformatRenderer.publishDate
    val iconUrl = watchPageData.watchPageInitialJSONData.contents.twoColumnWatchNextResults.results.results.contents[1].videoSecondaryInfoRenderer?.owner?.videoOwnerRenderer?.thumbnail?.thumbnails?.last()?.url

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.inversePrimary,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.padding(top = 5.dp),
                        fontSize = 20.sp,
                        text = videoDetails.title,
                        fontWeight = FontWeight.Bold
                    )
                    VideoDetailIconText(
                        modifier = Modifier.padding(top = 5.dp),
                        iconResId = R.drawable.ic_outline_today_24,
                        text = "${stringResource(id = R.string.publish_date)} : $publishDate"
                    )
                    VideoDetailIconText(
                        modifier = Modifier.padding(top = 5.dp),
                        iconResId = R.drawable.ic_outline_play_arrow_24,
                        text = "${stringResource(id = R.string.watch_count)} : ${videoDetails.viewCount}"
                    )
                }
                IconButton(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = { onOpenClick(!isExpanded) }
                ) { Icon(painter = painterResource(id = if (isExpanded) R.drawable.ic_outline_expand_less_24 else R.drawable.ic_outline_expand_more_24), contentDescription = null) }
            }
            // 動画説明文
            if (isExpanded) {
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    fontSize = 15.sp,
                    text = watchPageData.watchPageResponseJSONData.videoDetails.shortDescription,
                )
            }
            RoundedImageButton(
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp)
                    .fillMaxWidth(),
                mainText = videoDetails.author,
                subText = stringResource(id = R.string.publish_name),
                iconPainter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(iconUrl)
                        .apply { crossfade(true) }
                        .build()
                ),
                onClick = { onNavigate(NavigationLinkList.getChannelScreenLink(watchPageData.watchPageResponseJSONData.videoDetails.channelId)) }
            )
        }
    }
}

/**
 * 動画情報で使うメニュー。ダウンロードとかお気に入り登録とか
 *
 * @param modifier [Modifier]
 * @param watchPageData 視聴ページレスポンスデータ
 * @param onMenuClick 各メニュー押したら呼ばれる
 * */
@Composable
fun VideoDetailMenu(
    modifier: Modifier = Modifier,
    watchPageData: WatchPageData,
    onMenuClick: (BottomSheetInitData) -> Unit,
) {
    // プログレッシブ形式で配信してない場合はダウンロードボタンを塞ぐ
    val isDisableDownloadButton = watchPageData.isLiveContent()
    val isDownloadContent = watchPageData.type == "download"

    // ボタンリスト
    val buttonList = listOf(
        Triple(R.string.video_menu, R.drawable.ic_outline_more_vert_24, VideoPlayerMenuScreenInitData()),
        Triple(R.string.favourite, R.drawable.ic_outline_folder_special_24, AddVideoToFavoriteFolderScreenInitData(CommonVideoData(watchPageData.watchPageResponseJSONData))),
        Triple(
            if (isDownloadContent) R.string.downloaded_content else R.string.download,
            if (isDownloadContent) R.drawable.ic_outline_file_download_done_24 else R.drawable.chocodroid_download,
            if (isDisableDownloadButton || isDownloadContent) null else VideoDownloadScreenInitData(watchPageData)
        )
    )

    Row {
        buttonList.forEach { (stringResId, iconResId, data) ->

            val isDisable = data == null

            Surface(
                modifier = modifier
                    .padding(5.dp)
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.inversePrimary,
                content = {
                    if (isDisable) {
                        // 無効時のボタン
                        VideoDetailMenuDisableItem(modifier = Modifier.weight(1f))
                    } else {
                        VideoDetailMenuItem(
                            modifier = Modifier.weight(1f),
                            stringResId = stringResId,
                            iconResId = iconResId,
                            onClick = { data?.let { onMenuClick(it) } }
                        )
                    }
                }
            )
        }
    }

}

/**
 * 関連動画を表示する
 *
 * @param modifier [Modifier]
 * @param watchPageData 動画情報
 * @param onClick 押したとき。文字列は動画ID
 * @param onMenuClick 動画メニュー
 * */
@Composable
fun VideoDetailRecommendVideoList(
    modifier: Modifier = Modifier,
    watchPageData: WatchPageData,
    onClick: (String) -> Unit,
    onMenuClick: (BottomSheetInitData) -> Unit,
) {
    // 関連動画
    val relatedVideoList = watchPageData.watchPageInitialJSONData.contents.twoColumnWatchNextResults.secondaryResults.secondaryResults.results
        .mapNotNull { it.compactVideoRenderer }
        .map { compactVideoRenderer -> CommonVideoData(compactVideoRenderer) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
        content = {
            Column {
                relatedVideoList.forEach {
                    VideoListItem(
                        commonVideoData = it,
                        onClick = onClick,
                        onMenuClick = { onMenuClick(VideoListMenuScreenInitData(it)) }
                    )
                }
            }
        }
    )
}

/**
 * メニューの項目一つ一つ
 *
 * @param modifier [Modifier]
 * @param iconResId アイコンのリソースID
 * @param stringResId 文字列リソースID
 * @param onClick 押したとき
 * */
@Composable
private fun VideoDetailMenuItem(
    modifier: Modifier,
    stringResId: Int,
    iconResId: Int,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick,
            ),
        color = Color.Transparent,
        content = {
            Column(
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp, start = 10.dp, end = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(painter = painterResource(id = iconResId), contentDescription = null)
                Text(text = stringResource(id = stringResId), fontSize = 12.sp)
            }
        }
    )
}

/**
 * 利用できないメニューの項目
 *
 * @param modifier [Modifier]
 * */
@Composable
private fun VideoDetailMenuDisableItem(modifier: Modifier) {
    VideoDetailMenuItem(
        modifier = modifier,
        stringResId = R.string.unavailable,
        iconResId = R.drawable.ic_baseline_block_24,
        onClick = { }
    )
}

/**
 * テキストとアイコンを表示する
 *
 * @param modifier [Modifier]
 * @param iconResId アイコンのリソースID
 * @param text 文字列のリソースID
 * */
@Composable
private fun VideoDetailIconText(
    modifier: Modifier = Modifier,
    iconResId: Int,
    text: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(id = iconResId), contentDescription = null)
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = text)
    }
}