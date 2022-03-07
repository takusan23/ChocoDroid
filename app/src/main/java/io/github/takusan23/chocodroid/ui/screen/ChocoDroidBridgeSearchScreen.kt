package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeBar
import io.github.takusan23.chocodroid.ui.component.ChocoBridgeItem
import io.github.takusan23.chocodroid.ui.component.M3Scaffold

/**
 * 検索入力画面。検索結果は[SearchScreen]です
 *
 * @param onBack 戻ってほしいときに呼ばれる
 * @param onNavigate メイン画面のナビゲーションで実行してください
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChocoDroidBridgeSearchScreen(
    onBack: () -> Unit,
    navController: NavHostController,
) {
    // 入力した文字
    val searchText = remember { mutableStateOf("") }
    // テキストボックスにフォーカスがあたっているか
    val isFocusTextBox = remember { mutableStateOf(false) }
    // テキストボックスのフォーカス外すのにつかう
    val focusRequester = remember { FocusRequester() }

    // フォーカスを当てる
    LaunchedEffect(key1 = Unit, block = {
        focusRequester.requestFocus()
    })

    M3Scaffold {
        Column {
            ChocoBridgeBar(
                textValue = searchText.value,
                onTextChange = { searchText.value = it },
                onBackClick = onBack,
                focusRequester = focusRequester,
                isFocusTextBox = isFocusTextBox.value,
                onFocusChange = { isFocusTextBox.value = it },
                onSearchClick = { navController.navigate(NavigationLinkList.getSearchScreenLink(searchText.value)) }
            )
            ChocoBridgeItem(resIconId = R.drawable.ic_outline_search_24, text = stringResource(id = R.string.search)) {

            }
            ChocoBridgeItem(resIconId = R.drawable.ic_outline_play_arrow_24, text = stringResource(id = R.string.play_video_id)) {

            }
            ChocoBridgeItem(resIconId = R.drawable.ic_outline_content_paste_go_24, text = stringResource(id = R.string.paste_from_clipboard)) {

            }
            Divider(Modifier.padding(start = 10.dp, end = 10.dp))
        }
    }
}