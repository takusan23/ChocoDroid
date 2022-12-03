package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.BottomSheetInitData
import io.github.takusan23.chocodroid.ui.theme.SurfaceElevations
import io.github.takusan23.internet.data.watchpage.WatchPageData

/**
 * 動画説明部分のUI
 *
 * @param watchPageData 視聴ページレスポンスデータ
 * @param onLoadWatchPage 動画を再生してほしいときに呼ばれます。動画IDが渡されます
 * @param mainNavHostController メイン画面のNavController
 * @param miniPlayerState ミニプレイヤー操作
 * @param onBottomSheetNavigate ボトムシート表示のときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    watchPageData: WatchPageData,
    miniPlayerState: MiniPlayerState = rememberMiniPlayerState(),
    mainNavHostController: NavHostController = rememberNavController(),
    onLoadWatchPage: (String) -> Unit = {},
    onBottomSheetNavigate: (BottomSheetInitData) -> Unit = {},
) {
    // 動画説明文を展開するか
    val isExpandedDescription = remember { mutableStateOf(false) }

    M3Scaffold {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(SurfaceElevations.VideoDetailBackgroundElevation)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 動画情報カード
                item {
                    VideoDetailInfoCard(
                        modifier = Modifier.padding(top = 10.dp),
                        watchPageData = watchPageData,
                        onNavigate = {
                            mainNavHostController.navigate(it)
                            miniPlayerState.setState(MiniPlayerStateType.MiniPlayer)
                        },
                        isExpanded = isExpandedDescription.value,
                        onOpenClick = { isExpandedDescription.value = it }
                    )
                }
                // メニュー
                item {
                    VideoDetailMenu(
                        watchPageData = watchPageData,
                        onMenuClick = onBottomSheetNavigate
                    )
                }
                // 関連動画
                item {
                    VideoDetailRecommendVideoList(
                        watchPageData = watchPageData,
                        onClick = { onLoadWatchPage(it) },
                        onMenuClick = onBottomSheetNavigate
                    )
                }
            }
        }
    }
}