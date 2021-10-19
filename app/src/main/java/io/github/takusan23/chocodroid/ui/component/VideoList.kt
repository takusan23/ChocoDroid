package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.takusan23.htmlparse.data.search.VideoRenderer


/**
 * 動画一覧を表示する。マジComposeリスト表示が簡単で神。RecyclerViewとかありえんよな
 *
 * accompanistを使ってるのでSwipeToRefreshにも対応してます
 *
 * @param swipeRefreshState スワイプして更新のくるくるを手動で表示したい場合は
 * @param lazyListState スクロール制御など
 * @param videoList 動画一覧
 * @param onClick 押したら呼ばれる
 * @param onRefresh 引っ張って更新で引っ張ったら呼ばれる
 * */
@ExperimentalMaterialApi
@Composable
fun VideoList(
    lazyListState: LazyListState = rememberLazyListState(),
    swipeRefreshState: SwipeRefreshState = rememberSwipeRefreshState(false),
    videoList: List<VideoRenderer>,
    onClick: (String) -> Unit,
    onRefresh: (() -> Unit)? = null,
) {
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { onRefresh?.invoke() },
        content = {
            LazyColumn(
                state = lazyListState,
                content = {
                    items(videoList) { item ->
                        VideoListItem(videoRenderer = item, onClick = onClick)
                        Divider()
                    }
                }
            )
        }
    )
}

/**
 * 動画一覧で使う各項目のコンポーネント
 *
 * @param videoRenderer 動画情報
 * @param onClick 押したら呼ばれる
 * */
@ExperimentalMaterialApi
@Composable
fun VideoListItem(
    videoRenderer: VideoRenderer,
    onClick: (String) -> Unit
) = VideoListItem(
    videoId = videoRenderer.videoId,
    videoTitle = videoRenderer.title.runs.last().text,
    duration = videoRenderer.lengthText?.simpleText ?: "生放送です",
    watchCount = videoRenderer.viewCountText?.simpleText ?: videoRenderer.viewCountText?.runs?.joinToString(separator = " ") { it.text } ?: "",
    publishDate = videoRenderer.publishedTimeText?.simpleText ?: "---",
    ownerName = videoRenderer.ownerText.runs.last().text,
    thumbnailUrl = videoRenderer.thumbnail.thumbnails.last().url,
    durationTextBackground = if (videoRenderer.lengthText?.simpleText == null) Color.Red.copy(0.5f) else Color.Black.copy(0.5f),
    onClick = onClick
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
 * */
@ExperimentalMaterialApi
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
    onClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(videoId) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Glideの代わりにCoilを試す
            Box(
                modifier = Modifier.padding(5.dp)
            ) {
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
            Column(modifier = Modifier.padding(start = 5.dp)) {
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
        }
    }
}