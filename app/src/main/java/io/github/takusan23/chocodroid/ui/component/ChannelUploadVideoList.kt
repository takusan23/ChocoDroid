package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.takusan23.internet.data.channel.GridVideoRenderer

/**
 * チャンネル投稿動画一覧
 *
 * @param lazyListState スクロール位置の取得など
 * @param onRefresh 引っ張って更新したら呼ばれる
 * @param swipeRefreshState くるくる制御用
 * @param videoList 動画一覧
 * @param onClick 動画押したとき
 * */
@ExperimentalMaterialApi
@Composable
fun ChannelUploadVideoList(
    lazyListState: LazyListState = rememberLazyListState(),
    swipeRefreshState: SwipeRefreshState = rememberSwipeRefreshState(false),
    videoList: List<GridVideoRenderer>,
    onRefresh: (() -> Unit)? = null,
    onClick: (String) -> Unit,
) {
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { onRefresh?.invoke() },
        content = {
            LazyColumn(
                state = lazyListState,
                content = {
                    items(videoList) { item ->
                        ChannelUploadVideoListItem(item, onClick)
                    }
                }
            )
        }
    )
}

/**
 * 一覧表示の各コンポーネント
 *
 * @param gridVideoRenderer 動画情報
 * @param onClick 押したとき。引数は動画ID
 * */

/**
 * 一覧表示の各コンポーネント
 *
 * @param gridVideoRenderer 動画情報
 * @param onClick 押したとき。引数は動画ID
 * */
@ExperimentalMaterialApi
@Composable
fun ChannelUploadVideoListItem(
    gridVideoRenderer: GridVideoRenderer,
    onClick: (String) -> Unit,
) = VideoListItem(
    videoId = gridVideoRenderer.videoId,
    videoTitle = gridVideoRenderer.title.runs[0].text,
    duration = gridVideoRenderer.thumbnailOverlays[0].thumbnailOverlayTimeStatusRenderer!!.text.simpleText,
    watchCount = gridVideoRenderer.viewCountText.simpleText,
    publishDate = gridVideoRenderer.publishedTimeText.simpleText,
    ownerName = "",
    thumbnailUrl = gridVideoRenderer.thumbnail.thumbnails.last().url,
    onClick = onClick
)