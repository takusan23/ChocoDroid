package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.tool.ClipboardTool
import io.github.takusan23.chocodroid.ui.screen.NavigationLinkList
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * 引数多いのでViewModel/NavControllerへ直接指定するバージョン。
 *
 * AtomicDesign？知らねえ！
 *
 * @param modifier Modifier
 * @param viewModel メイン画面のViewModel
 * @param navHostController メイン画面のNavController
 * */
@ExperimentalMaterialApi
@Composable
fun ChocoBridgeBar(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val textValue = remember { mutableStateOf("") }

    ChocoBridgeBar(
        modifier = modifier,
        textValue = textValue.value,
        onTextChange = { after -> textValue.value = after },
        onPlayClick = {
            if (it.isNotEmpty()) {
                viewModel.loadWatchPage(it)
            }
        },
        onSearchClick = {
            if (it.isNotEmpty()) {
                navHostController.navigate("${NavigationLinkList.SearchScreen}?query=${it}")
            }
        },
        onClipboardClick = { textValue.value = ClipboardTool.getClipboardText(context) ?: "" }
    )
}


/**
 * 検索 URL直打ち 動画ID などを一つにまとめて！
 *
 * @param textValue テキストボックスの中身
 * @param onTextChange テキストボックスの中身が変わったら呼ばれる
 * @param onSearchClick 検索押したら
 * @param onPlayClick 動画IDを再生を押したら
 * @param onClipboardClick クリップボード取得押したら
 * @param modifier Modifier
 * */
@ExperimentalMaterialApi
@Composable
fun ChocoBridgeBar(
    modifier: Modifier = Modifier,
    textValue: String,
    onTextChange: (String) -> Unit,
    onSearchClick: ((String) -> Unit)? = null,
    onPlayClick: ((String) -> Unit)? = null,
    onClipboardClick: (() -> Unit)? = null,
) {
    // テキストボックスにフォーカスがあたっているか
    val isFocusTextBox = remember { mutableStateOf(false) }
    // テキストボックスのフォーカス外すのにつかう
    val focusRequester = remember { FocusRequester() }
    // キーボード消す
    val inputService = LocalTextInputService.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        color = MaterialTheme.colors.primary,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(56.dp)
            ) {
                // フォーカスある状態なら戻るを表示
                if (isFocusTextBox.value) {
                    IconButton(
                        modifier = Modifier
                            .padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
                            .focusRequester(focusRequester)
                            .focusTarget(),
                        onClick = {
                            println(focusRequester.requestFocus())
                            inputService?.hideSoftwareKeyboard()
                        },
                        content = { Icon(painter = painterResource(id = R.drawable.ic_outline_arrow_back_24), contentDescription = "クリア") }
                    )
                } else {
                    Spacer(modifier = Modifier.padding(start = 20.dp))
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (textValue.isEmpty() && !isFocusTextBox.value) {
                        // ヒント代わり
                        Text(text = stringResource(id = R.string.choco_bridge_bar_title))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { isFocusTextBox.value = it.isFocused },
                            value = textValue,
                            maxLines = 1,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { onSearchClick?.invoke(textValue) }),
                            onValueChange = onTextChange,
                        )
                        // クリアボタン
                        if (isFocusTextBox.value) {
                            IconButton(
                                modifier = Modifier
                                    .padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
                                onClick = { onTextChange("") }
                            ) {
                                Icon(painter = painterResource(id = R.drawable.ic_outline_clear_24), contentDescription = "クリア")
                            }
                        }
                    }
                }
            }

            // ここにクリップボード貼り付けなどを入れる
            if (isFocusTextBox.value) {
                ChocoNavMenuItem(resIconId = R.drawable.ic_outline_search_24, text = stringResource(id = R.string.search), onClick = { onSearchClick?.invoke(textValue) })
                ChocoNavMenuItem(resIconId = R.drawable.ic_outline_play_arrow_24, text = stringResource(id = R.string.play_video_id), onClick = { onPlayClick?.invoke(textValue) })
                Divider(Modifier.padding(start = 10.dp, end = 10.dp))
                ChocoNavMenuItem(resIconId = R.drawable.ic_outline_content_paste_go_24, text = stringResource(id = R.string.paste_from_clipboard), onClick = { onClipboardClick?.invoke() })
                Spacer(modifier = Modifier.padding(bottom = 10.dp))
            }
        }
    }
}

/**
 * 検索押したときに出るあのメニュー
 * */
@ExperimentalMaterialApi
@Composable
fun ChocoNavMenuItem(resIconId: Int, text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.padding(10.dp),
                painter = painterResource(id = resIconId), contentDescription = null
            )
            Text(
                modifier = Modifier.padding(10.dp),
                text = text
            )
        }
    }
}