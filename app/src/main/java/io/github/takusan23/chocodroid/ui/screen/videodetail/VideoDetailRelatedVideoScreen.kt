package io.github.takusan23.chocodroid.ui.screen.videodetail

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import io.github.takusan23.chocodroid.ui.component.RelatedVideoList
import io.github.takusan23.internet.data.watchpage.WatchPageData

/**
 * 関連動画表示画面
 *
 * @param watchPageData 動画情報
 * @param onClick 押したときに呼ばれる。引数は動画ID
 * */
@ExperimentalMaterialApi
@Composable
fun VideoDetailRelatedVideoScreen(
    watchPageData: WatchPageData,
    onClick: (String) -> Unit,
) {
    // 関連動画
    val relatedVideoList = watchPageData.watchPageInitialJSONData.contents.twoColumnWatchNextResults.secondaryResults.secondaryResults.results.mapNotNull { it.compactVideoRenderer }
    RelatedVideoList(list = relatedVideoList, onClick = onClick)
}