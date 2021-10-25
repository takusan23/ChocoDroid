package io.github.takusan23.chocodroid.ui.screen

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.ChannelHeader
import io.github.takusan23.chocodroid.ui.component.VideoList
import io.github.takusan23.chocodroid.viewmodel.ChannelScreenViewModel

/**
 * チャンネル投稿動画画面
 *
 * @param onClick 動画項目押したとき。引数は動画ID
 * @param channelScreenViewModel チャンネル画面ViewModel
 * */
@ExperimentalMaterialApi
@Composable
fun ChannelScreen(channelScreenViewModel: ChannelScreenViewModel, onClick: (String) -> Unit) {

    val channelResponseData = channelScreenViewModel.channelResponseDataFlow.collectAsState()
    val uploadVideoList = channelScreenViewModel.uploadVideoListFlow.collectAsState()
    val isLoading = channelScreenViewModel.isLoadingFlow.collectAsState()
    val errorMessage = channelScreenViewModel.errorMessageFlow.collectAsState()

    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val lazyListState = rememberLazyListState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading.value)

    // エラー時
    LaunchedEffect(key1 = errorMessage.value, block = {
        if (errorMessage.value != null) {
            val snackbarHostState = scaffoldState.snackbarHostState
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

    Scaffold(
        scaffoldState = scaffoldState,
        content = {
            Column(modifier = Modifier.padding(it)) {
                // ヘッダー部
                channelResponseData.value?.apply {
                    ChannelHeader(
                        header = this.header.c4TabbedHeaderRenderer,
                        onClickOpenBrowser = {
                            // ブラウザで開く
                            val intent = Intent(Intent.ACTION_VIEW, "https://www.youtube.com/channel/${this.header.c4TabbedHeaderRenderer.channelId}".toUri())
                            context.startActivity(intent)
                        }
                    )
                }
                Divider()
                // 動画配列
                VideoList(
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