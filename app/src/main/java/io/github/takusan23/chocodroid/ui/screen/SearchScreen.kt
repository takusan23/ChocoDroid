package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.ui.component.SearchScreenBar
import io.github.takusan23.chocodroid.ui.component.VideoList
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.BottomSheetInitData
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.SearchSortScreenInitData
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.VideoListMenuScreenInitData
import io.github.takusan23.chocodroid.viewmodel.SearchScreenViewModel
import kotlinx.coroutines.launch

/**
 * 検索画面
 *
 * @param viewModel 検索画面ViewModel
 * @param onClick 押したときに呼ばれる。引数は動画ID
 * @param onBack 戻る際に呼ばれる
 * @param onBottomSheetNavigate ボトムシート出してほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel,
    onBack: () -> Unit,
    onClick: (String) -> Unit,
    onBottomSheetNavigate: (BottomSheetInitData) -> Unit,
) {
    val videoList = viewModel.searchResultListFlow.collectAsState()
    val isLoading = viewModel.isLoadingFlow.collectAsState(initial = false)
    val errorMessage = viewModel.errorMessageFlow.collectAsState()
    val query = viewModel.queryFlow.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading.value)
    val scope = rememberCoroutineScope()

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
        LaunchedEffect(key1 = Unit, block = { viewModel.moreLoad() })
    }

    M3Scaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            SearchScreenBar(
                onBack = onBack,
                onSearch = { scope.launch { viewModel.reSearch() } },
                onSortChange = { onBottomSheetNavigate(SearchSortScreenInitData()) },
                onSearchWordChange = { viewModel.setQuery(it) },
                searchWord = query.value,
            )
        },
        content = {
            Surface(
                modifier = Modifier
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                    .fillMaxHeight(),
                color = MaterialTheme.colorScheme.inverseOnSurface,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                content = {
                    VideoList(
                        swipeRefreshState = swipeRefreshState,
                        lazyListState = lazyListState,
                        videoList = videoList.value,
                        onRefresh = { scope.launch { viewModel.reSearch() } },
                        onClick = onClick,
                        onMenuClick = { onBottomSheetNavigate(VideoListMenuScreenInitData(it)) }
                    )
                }
            )
        }
    )
}