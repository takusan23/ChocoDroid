package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.takusan23.chocodroid.R
import io.github.takusan23.internet.data.CommonVideoData


/**
 * 動画一覧を表示する。マジComposeリスト表示が簡単で神。RecyclerViewとかありえんよな
 *
 * accompanistを使ってるのでSwipeToRefreshにも対応してます
 *
 * @param swipeRefreshState スワイプして更新のくるくるを手動で表示したい場合は
 * @param lazyListState スクロール制御など
 * @param videoList 動画一覧
 * @param onClick 押したら呼ばれる。動画IDが渡されます
 * @param isSwipeEnabled 引っ張るやつ無効にする場合はtrue
 * @param onRefresh 引っ張って更新で引っ張ったら呼ばれる
 * @param onMenuClick メニュー押したとき。nullにした場合はメニューを非表示にします
 * */
@Composable
fun VideoList(
    lazyListState: LazyListState = rememberLazyListState(),
    swipeRefreshState: SwipeRefreshState = rememberSwipeRefreshState(false),
    videoList: List<CommonVideoData>,
    onClick: (String) -> Unit,
    onMenuClick: ((CommonVideoData) -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
    isSwipeEnabled: Boolean = onRefresh != null,
) {
    SwipeRefresh(
        swipeEnabled = isSwipeEnabled,
        state = swipeRefreshState,
        onRefresh = { onRefresh?.invoke() },
        content = {
            LazyColumn(
                state = lazyListState,
                content = {
                    items(videoList) { item ->
                        VideoListItem(
                            commonVideoData = item,
                            onClick = onClick,
                            onMenuClick = onMenuClick
                        )
                    }
                }
            )
        }
    )
}

/**
 * 動画一覧で使う各項目のコンポーネント
 *
 * @param commonVideoData 動画情報
 * @param onClick 押したら呼ばれる
 * @param onMenuClick メニュー押したとき。nullにした場合はメニューを非表示にします
 * */
@Composable
fun VideoListItem(
    commonVideoData: CommonVideoData,
    onClick: (String) -> Unit,
    onMenuClick: ((CommonVideoData) -> Unit)? = null,
) = VideoListItem(
    videoId = commonVideoData.videoId,
    videoTitle = commonVideoData.videoTitle,
    duration = commonVideoData.duration ?: stringResource(id = R.string.live),
    watchCount = commonVideoData.watchCount,
    publishDate = commonVideoData.publishDate ?: stringResource(id = R.string.live),
    ownerName = commonVideoData.ownerName,
    thumbnailUrl = commonVideoData.thumbnailUrl,
    durationTextBackground = if (commonVideoData.duration == null) Color.Red.copy(0.5f) else Color.Black.copy(0.5f),
    onClick = onClick,
    onMenuClick = if (onMenuClick != null) {
        { onMenuClick(commonVideoData) }
    } else null,
)

/**
 * データクラスに依存しないリストの各コンポーネント
 *
 * @param videoId 動画ID
 * @param thumbnailUrl サムネイルURL
 * @param videoTitle タイトル
 * @param watchCount 視聴回数
 * @param publishDate 投稿日
 * @param ownerName 投稿者。変態糞土方
 * @param duration 動画時間
 * @param durationTextBackground 再生時間の背景色。生放送との分岐でどうぞ
 * @param onClick 押したとき。引数は動画ID
 * @param onMenuClick メニュー押したとき。nullにした場合はメニューを非表示にします
 * */
@Composable
fun VideoListItem(
    videoId: String,
    videoTitle: String,
    duration: String,
    watchCount: String,
    publishDate: String,
    ownerName: String,
    thumbnailUrl: String,
    durationTextBackground: Color = Color.Black.copy(0.5f),
    onClick: (String) -> Unit,
    onMenuClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(),
        onClick = { onClick(videoId) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Glideの代わりにCoilを試す
            Box(modifier = Modifier.padding(5.dp)) {
                Image(
                    modifier = Modifier
                        .width(150.dp)
                        .aspectRatio(1.7f)
                        .clip(RoundedCornerShape(10.dp)),
                    painter = rememberImagePainter(
                        data = thumbnailUrl,
                        builder = {
                            crossfade(true)
                        }
                    ),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier
                        .offset((-5).dp, (-5).dp)
                        .background(color = durationTextBackground, shape = RoundedCornerShape(2.dp))
                        .align(alignment = Alignment.BottomEnd),
                    color = Color.White,
                    text = duration
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp)
            ) {
                Text(
                    modifier = Modifier.padding(0.dp),
                    fontSize = 16.sp,
                    text = videoTitle,
                    maxLines = 2
                )
                Text(
                    modifier = Modifier.padding(0.dp),
                    fontSize = 14.sp,
                    text = "$watchCount / $publishDate"
                )
                Text(
                    modifier = Modifier.padding(0.dp),
                    fontSize = 14.sp,
                    text = ownerName
                )
            }
            if (onMenuClick != null) {
                IconButton(
                    modifier = Modifier.padding(start = 5.dp, end = 10.dp),
                    onClick = onMenuClick
                ) { Icon(painter = painterResource(id = io.github.takusan23.chocodroid.R.drawable.ic_outline_more_vert_24), contentDescription = null) }
            }
        }
    }
}