package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.tool.ClipboardTool
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeItem
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeSuggestItem
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.viewmodel.ChocoBridgeSearchScreenViewModel
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * 検索入力画面。検索結果は[SearchScreen]です
 *
 * @param mainViewModel メイン画面のViewModel
 * @param navController メイン画面のナビゲーション
 * @param bridgeSearchScreenViewModel 検索入力画面のViewModel
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChocoBridgeSearchScreen(
    mainViewModel: MainScreenViewModel,
    navController: NavHostController,
    bridgeSearchScreenViewModel: ChocoBridgeSearchScreenViewModel,
) {
    val context = LocalContext.current
    // 入力した文字
    val searchText = remember { mutableStateOf("") }
    // テキストボックスにフォーカスがあたっているか
    val isFocusTextBox = remember { mutableStateOf(false) }
    // テキストボックスのフォーカス外すのにつかう
    val focusRequester = remember { FocusRequester() }
    // 検索ワードのサジェスト
    val suggestWordList = bridgeSearchScreenViewModel.searchSuggestList.collectAsState()

    // フォーカスを当てる
    LaunchedEffect(key1 = Unit, block = {
        focusRequester.requestFocus()
    })

    /**
     * 検索する関数
     *
     * @param searchWord 検索ワード
     * */
    fun search(searchWord: String) {
        navController.navigate(NavigationLinkList.getSearchScreenLink(searchWord))
    }

    M3Scaffold {
        Column {
            ChocoBridgeBar(
                textValue = searchText.value,
                onBackClick = { navController.popBackStack() },
                focusRequester = focusRequester,
                isFocusTextBox = isFocusTextBox.value,
                onFocusChange = { isFocusTextBox.value = it },
                onSearchClick = { search(searchText.value) },
                onTextChange = {
                    searchText.value = it
                    bridgeSearchScreenViewModel.getSuggestWord(it)
                },
                suggestContent = {
                    Column {
                        suggestWordList.value.forEach { suggestWord ->
                            ChocoBridgeSuggestItem(text = suggestWord) { search(suggestWord) }
                        }
                        if (suggestWordList.value.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            )
            ChocoBridgeItem(resIconId = R.drawable.ic_outline_search_24, text = stringResource(id = R.string.search)) {
                search(searchText.value)
            }
            ChocoBridgeItem(resIconId = R.drawable.ic_outline_play_arrow_24, text = stringResource(id = R.string.play_video_id)) {
                mainViewModel.loadWatchPage(searchText.value)
            }
            ChocoBridgeItem(resIconId = R.drawable.ic_outline_content_paste_go_24, text = stringResource(id = R.string.paste_from_clipboard)) {
                ClipboardTool.getClipboardText(context)?.also { searchText.value = it }
            }
            Divider(Modifier.padding(start = 10.dp, end = 10.dp))
        }
    }
}