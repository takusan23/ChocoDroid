package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.VideoListItem
import io.github.takusan23.chocodroid.viewmodel.SearchScreenViewModel

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
                title = { Text(text = "検索結果") },
                elevation = 0.dp,
                backgroundColor = MaterialTheme.colors.background
            )
        },
        content = {
            if (isLoading.value) {
                LoadingScreen()
            } else {
                LazyColumn(content = {
                    items(videoList.value) { videoItem ->
                        val videoData = videoItem.videoRenderer
                        VideoListItem(
                            videoId = videoData.videoId,
                            title = videoData.title.runs[0].text,
                            thumbnailUrl = videoData.thumbnail.thumbnails.last().url,
                            ownerName = videoData.ownerText.runs[0].text,
                            onClick = onClick
                        )
                        Divider()
                    }
                })
            }
        }
    )

}