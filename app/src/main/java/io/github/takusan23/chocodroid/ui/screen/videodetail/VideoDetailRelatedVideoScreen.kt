package io.github.takusan23.chocodroid.ui.screen.videodetail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.component.RelatedVideoList
import io.github.takusan23.internet.data.watchpage.WatchPageData

/**
 * 関連動画表示画面
 *
 * @param watchPageData 動画情報
 * @param onClick 押したときに呼ばれる。引数は動画ID
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailRelatedVideoScreen(
    watchPageData: WatchPageData,
    onClick: (String) -> Unit,
) {
    // 関連動画
    val relatedVideoList = watchPageData.watchPageInitialJSONData.contents.twoColumnWatchNextResults.secondaryResults.secondaryResults.results.mapNotNull { it.compactVideoRenderer }
    M3Scaffold {
        RelatedVideoList(list = relatedVideoList, onClick = onClick)
    }
}