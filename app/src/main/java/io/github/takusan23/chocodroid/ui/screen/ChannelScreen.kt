package io.github.takusan23.chocodroid.ui.screen

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.ChannelHeader
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.component.VideoList
import io.github.takusan23.chocodroid.viewmodel.ChannelScreenViewModel

/**
 * チャンネル投稿動画画面
 *
 * @param onClick 動画項目押したとき。引数は動画ID
 * @param channelScreenViewModel チャンネル画面ViewModel
 * @param onBack 戻ってほしいときに呼ばれます
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelScreen(channelScreenViewModel: ChannelScreenViewModel, onClick: (String) -> Unit, onBack: () -> Unit) {

    val channelResponseData = channelScreenViewModel.channelResponseDataFlow.collectAsState()
    val uploadVideoList = channelScreenViewModel.uploadVideoListFlow.collectAsState()
    val isLoading = channelScreenViewModel.isLoadingFlow.collectAsState(initial = false)
    val errorMessage = channelScreenViewModel.errorMessageFlow.collectAsState()
    val isAddedFavoriteCh = channelScreenViewModel.isAddedFavoriteChFlow.collectAsState(initial = false)

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading.value)

    // エラー時
    LaunchedEffect(key1 = errorMessage.value, block = {
        if (errorMessage.value != null) {
            val result = snackbarHostState.showSnackbar(errorMessage.value!!, context.getString(R.string.close), SnackbarDuration.Indefinite)
            if (result == SnackbarResult.ActionPerformed) {
                snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    })

    // 追加読み込み制御
    if (lazyListState.firstVisibleItemIndex > 0 && lazyListState.firstVisibleItemIndex + lazyListState.layoutInfo.visibleItemsInfo.size == lazyListState.layoutInfo.totalItemsCount) {
        // 追加読み込み
        LaunchedEffect(key1 = Unit, block = { channelScreenViewModel.moreUploadVideoLoad() })
    }

    M3Scaffold(
        snackbarHostState = snackbarHostState,
        content = {
            Column(modifier = Modifier.padding(it)) {
                // 動画配列
                VideoList(
                    headerLayout = {
                        // ヘッダー部
                        channelResponseData.value?.apply {
                            ChannelHeader(
                                header = this.header.c4TabbedHeaderRenderer,
                                isAddedFavoriteCh = isAddedFavoriteCh.value,
                                onOpenBrowserClick = {
                                    // ブラウザで開く
                                    val intent = Intent(Intent.ACTION_VIEW, "https://www.youtube.com/channel/${this.header.c4TabbedHeaderRenderer.channelId}".toUri())
                                    context.startActivity(intent)
                                },
                                onAddFavoriteChClick = {
                                    // 登録する？
                                    if (isAddedFavoriteCh.value) {
                                        channelScreenViewModel.deleteFavoriteChDB()
                                    } else {
                                        channelScreenViewModel.addFavoriteChDB()
                                    }
                                }
                            )
                        }
                        Divider()
                    },
                    lazyListState = lazyListState,
                    swipeRefreshState = swipeRefreshState,
                    videoList = uploadVideoList.value,
                    onRefresh = { channelScreenViewModel.getReGetUploadVideo() },
                    onClick = onClick
                )
            }
        }
    )

}