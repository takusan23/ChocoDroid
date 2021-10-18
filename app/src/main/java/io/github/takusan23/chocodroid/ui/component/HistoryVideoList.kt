package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import io.github.takusan23.chocodroid.database.entity.HistoryDBEntity

/**
 * 履歴一覧表示コンポーネント
 *
 * @param lazyListState スクロール制御など
 * @param historyDBEntityList 履歴一覧
 * @param onClick 押したとき。引数は動画ID
 * */
@ExperimentalMaterialApi
@Composable
fun HistoryVideoList(
    lazyListState: LazyListState = rememberLazyListState(),
    historyDBEntityList: List<HistoryDBEntity>,
    onClick: (String) -> Unit,
) {
    LazyColumn(
        state = lazyListState,
        content = {
            items(historyDBEntityList) { item ->
                HistoryVideoListItem(historyDBEntity = item, onClick = onClick)
            }
        }
    )
}

/**
 * 一覧表示の各コンポーネント
 *
 * @param historyDBEntity 履歴情報
 * @param onClick 押したとき。引数は動画ID
 * */
@ExperimentalMaterialApi
@Composable
fun HistoryVideoListItem(historyDBEntity: HistoryDBEntity, onClick: (String) -> Unit) = VideoListItem(
    videoId = historyDBEntity.videoId,
    videoTitle = historyDBEntity.title,
    duration = historyDBEntity.duration,
    watchCount = "${historyDBEntity.localWatchCount} 回 視聴",
    publishDate = historyDBEntity.publishedDate,
    ownerName = historyDBEntity.ownerName,
    thumbnailUrl = historyDBEntity.thumbnailUrl,
    onClick = onClick
)