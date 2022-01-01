package io.github.takusan23.chocodroid.ui.component

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.screen.NavigationLinkList
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.AddVideoToFavoriteFolderScreenInitData
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.BottomSheetInitData
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.VideoDownloadScreenInitData
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.VideoListMenuScreenInitData
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
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(10.dp),
        content = {
            Column(modifier = Modifier.padding(10.dp)) {
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        VideoDetailIconText(
                            modifier = Modifier.padding(top = 5.dp),
                            iconResId = R.drawable.ic_outline_today_24,
                            text = "${stringResource(id = R.string.publish_date)} : $publishDate"
                        )
                        Text(
                            modifier = Modifier.padding(top = 5.dp),
                            fontSize = 20.sp,
                            text = videoDetails.title,
                        )
                        VideoDetailIconText(
                            modifier = Modifier.padding(top = 5.dp),
                            iconResId = R.drawable.ic_outline_play_arrow_24,
                            text = "${stringResource(id = R.string.watch_count)} : ${videoDetails.viewCount}"
                        )
                        // 動画説明文
                        if (isExpanded) {
                            Text(
                                modifier = Modifier.padding(top = 5.dp),
                                fontSize = 15.sp,
                                text = watchPageData.watchPageResponseJSONData.videoDetails.shortDescription,
                            )
                        }
                    }
                    IconButton(
                        modifier = Modifier.padding(top = 10.dp),
                        onClick = { onOpenClick(!isExpanded) }
                    ) { Icon(painter = painterResource(id = if (isExpanded) R.drawable.ic_outline_expand_less_24 else R.drawable.ic_outline_expand_more_24), contentDescription = null) }
                }
                RoundedImageButton(
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .fillMaxWidth(),
                    mainText = videoDetails.author,
                    subText = stringResource(id = R.string.publish_name),
                    iconPainter = rememberImagePainter(
                        data = iconUrl,
                        builder = { crossfade(true) }
                    ),
                    onClick = { onNavigate(NavigationLinkList.getChannelScreenLink(watchPageData.watchPageResponseJSONData.videoDetails.channelId)) }
                )
            }
        }
    )
}

/**
 * 動画情報で使うメニュー。ダウンロードとかお気に入り登録とか
 *
 * @param modifier [Modifier]
 * @param onMenuClick 各メニュー押したら呼ばれる
 * */
@Composable
fun VideoDetailMenu(
    modifier: Modifier = Modifier,
    watchPageData: WatchPageData,
    onMenuClick: (BottomSheetInitData) -> Unit,
) {
    // ボタンリスト
    val buttonList = listOf(
        Triple(R.string.video_menu, R.drawable.ic_outline_more_vert_24, null),
        Triple(R.string.favourite, R.drawable.ic_outline_folder_special_24, AddVideoToFavoriteFolderScreenInitData(CommonVideoData(watchPageData.watchPageResponseJSONData))),
        Triple(R.string.download, R.drawable.chocodroid_download, VideoDownloadScreenInitData(watchPageData))
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(10.dp),
        content = {
            Row {
                buttonList.forEach { (stringResId, iconResId, data) ->
                    VideoDetailMenuItem(
                        modifier = Modifier.weight(1f),
                        stringResId = stringResId,
                        iconResId = iconResId,
                        onClick = { data?.let { onMenuClick(it) } }
                    )
                }
            }
        }
    )
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
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(10.dp),
        content = {
            Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
                relatedVideoList.forEach {
                    VideoListItem(
                        commonVideoData = it,
                        onClick = onClick,
                        onMenuClick = { onMenuClick(VideoListMenuScreenInitData(it.videoId, it.videoTitle)) }
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
private fun VideoDetailMenuItem(modifier: Modifier, stringResId: Int, iconResId: Int, onClick: () -> Unit) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(),
        content = {
            Column(
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp, start = 10.dp, end = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(painter = painterResource(id = iconResId), contentDescription = null)
                Text(text = stringResource(id = stringResId))
            }
        }
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