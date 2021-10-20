package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import io.github.takusan23.htmlparse.data.watchpage.CompactVideoRenderer

/**
 * 関連動画一覧
 *
 * @param state スクロールなど
 * @param list 関連動画
 * */
@ExperimentalMaterialApi
@Composable
fun RelatedVideoList(
    state: LazyListState = rememberLazyListState(),
    list: List<CompactVideoRenderer>,
    onClick: (String) -> Unit,
) {
    LazyColumn(
        state = state,
        content = {
            items(list) { item ->
                RelatedVideoListItem(
                    compactVideoRenderer = item,
                    onClick = onClick
                )
            }
        }
    )
}

/**
 * 関連動画一覧の各コンポーネント
 *
 * @param state スクロールなど
 * @param list 関連動画
 * */
@ExperimentalMaterialApi
@Composable
private fun RelatedVideoListItem(
    compactVideoRenderer: CompactVideoRenderer,
    onClick: (String) -> Unit,
) {
    VideoListItem(
        videoId = compactVideoRenderer.videoId,
        videoTitle = compactVideoRenderer.title.simpleText,
        duration = compactVideoRenderer.lengthText.simpleText,
        watchCount = compactVideoRenderer.shortViewCountText.simpleText,
        publishDate = compactVideoRenderer.publishedTimeText.simpleText,
        ownerName = compactVideoRenderer.longBylineText.runs[0].text,
        thumbnailUrl = compactVideoRenderer.thumbnail.thumbnails.last().url,
        onClick = onClick
    )
}