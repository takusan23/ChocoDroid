package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.VideoList
import io.github.takusan23.chocodroid.ui.component.VideoListItem
import io.github.takusan23.chocodroid.viewmodel.SearchScreenViewModel
import kotlinx.coroutines.launch

/**
 * 検索画面
 *
 * @param viewModel 検索画面ViewModel
 * @param onClick 押したときに呼ばれる。引数は動画ID
 * */
@ExperimentalMaterialApi
@Composable
fun SearchScreen(viewModel: SearchScreenViewModel, onBack: () -> Unit, onClick: (String) -> Unit) {
    val videoList = viewModel.searchResultListFlow.collectAsState()
    val isLoading = viewModel.isLoadingFlow.collectAsState()
    val errorMessage = viewModel.errorMessageFlow.collectAsState()

    val scaffoldState = rememberScaffoldState()
    val lazyListState = rememberLazyListState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading.value)
    val scope = rememberCoroutineScope()

    // エラー時
    LaunchedEffect(key1 = errorMessage.value, block = {
        if (errorMessage.value != null) {
            val snackbarHostState = scaffoldState.snackbarHostState
            val result = snackbarHostState.showSnackbar(errorMessage.value!!, "閉じる", SnackbarDuration.Indefinite)
            if (result == SnackbarResult.ActionPerformed) {
                snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    })

    // 追加読み込み制御
    if (lazyListState.firstVisibleItemIndex > 0 && lazyListState.firstVisibleItemIndex + lazyListState.layoutInfo.visibleItemsInfo.size == lazyListState.layoutInfo.totalItemsCount) {
        // 追加読み込み
        LaunchedEffect(key1 = Unit, block = { viewModel.moreLoad() })
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        content = { Icon(painter = painterResource(id = R.drawable.ic_outline_arrow_back_24), contentDescription = null) }
                    )
                },
                title = { /*Text(text = "検索結果")*/ },
                elevation = 0.dp,
                backgroundColor = MaterialTheme.colors.background
            )
        },
        content = {
            VideoList(
                swipeRefreshState = swipeRefreshState,
                lazyListState = lazyListState,
                videoList = videoList.value.map { it.videoRenderer },
                onRefresh = { scope.launch { viewModel.reSearch() } },
                onClick = onClick
            )
        }
    )

}