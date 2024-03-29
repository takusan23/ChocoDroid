package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.tool.ClipboardTool
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeItem
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeSuggestItem
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.chocodroid.viewmodel.ChocoBridgeSearchScreenViewModel

/**
 * 検索入力画面。検索結果は[SearchScreen]です
 *
 * @param navController メイン画面のナビゲーション
 * @param bridgeSearchScreenViewModel 検索入力画面のViewModel
 * @param onLoadWatchPage 動画IDが渡されるのでロードする
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChocoBridgeSearchScreen(
    navController: NavHostController,
    bridgeSearchScreenViewModel: ChocoBridgeSearchScreenViewModel,
    onLoadWatchPage: (String) -> Unit = {},
) {
    val context = LocalContext.current
    // カーソルの位置を変更するには TextFieldValue が必要
    val _textFieldValue = remember { mutableStateOf(TextFieldValue()) }
    // カーソルの位置
    val cursorPos = remember { mutableStateOf(TextRange(0)) }
    // 変換文字列
    val textComposition = remember { mutableStateOf<TextRange?>(null) }
    // テキストボックスにフォーカスがあたっているか
    val isFocusTextBox = remember { mutableStateOf(false) }
    // テキストボックスのフォーカス外すのにつかう
    val focusRequester = remember { FocusRequester() }
    // 入力した文字
    val searchText = bridgeSearchScreenViewModel.searchWord.collectAsState()
    // カーソルの位置と入力文字どっちかの値が更新されたら再生成される
    val textFieldValue = _textFieldValue.value.copy(
        text = searchText.value,
        selection = cursorPos.value,
        composition = textComposition.value
    )
    // 検索ワードのサジェスト
    val suggestWordList = bridgeSearchScreenViewModel.searchSuggestList.collectAsState()


    /**
     * 検索する関数
     *
     * @param searchWord 検索ワード
     * */
    fun search(searchWord: String) {
        navController.navigate(NavigationLinkList.getSearchScreenLink(searchWord))
    }

    // 初回コンポーズ時にやること
    LaunchedEffect(key1 = Unit) {
        // フォーカスを当てる
        focusRequester.requestFocus()
        // キャレットの位置を文字の最後にする
        cursorPos.value = TextRange(searchText.value.length)
    }

    M3Scaffold {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            ChocoBridgeBar(
                textValue = textFieldValue,
                focusRequester = focusRequester,
                isFocusTextBox = isFocusTextBox.value,
                onBackClick = { navController.popBackStack() },
                onFocusChange = { isFocusTextBox.value = it },
                onSearchClick = { search(searchText.value) },
                onTextChange = {
                    if (textFieldValue != it) {
                        // 違うとき
                        bridgeSearchScreenViewModel.getSuggestWord(it.text)
                    }
                    bridgeSearchScreenViewModel.setSearchWord(it.text)
                    cursorPos.value = it.selection
                    textComposition.value = it.composition
                },
                suggestContent = {
                    Column {
                        suggestWordList.value.forEach { suggestWord ->
                            ChocoBridgeSuggestItem(
                                text = suggestWord,
                                onSearchClick = { search(suggestWord) },
                                onAppendClick = {
                                    bridgeSearchScreenViewModel.setSearchWord(it)
                                    bridgeSearchScreenViewModel.getSuggestWord(it)
                                    // キャレットの位置を文字の最後にする
                                    cursorPos.value = TextRange(it.length)
                                    textComposition.value = null
                                }
                            )
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
                onLoadWatchPage(searchText.value)
            }
            ChocoBridgeItem(resIconId = R.drawable.ic_outline_content_paste_go_24, text = stringResource(id = R.string.paste_from_clipboard)) {
                ClipboardTool.getClipboardText(context)?.also { bridgeSearchScreenViewModel.setSearchWord(it) }
            }
            Divider(Modifier.padding(start = 10.dp, end = 10.dp))
        }
    }
}