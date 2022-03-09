package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.takusan23.chocodroid.R

/**
 * アプリバー。これを押すと検索入力画面とかに飛ぶ
 *
 * @param modifier Modifier
 * @param viewModel メイン画面のViewModel
 * @param navHostController メイン画面のNavController
 * */
@Composable
fun ChocoBridgeBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onSettingClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.inversePrimary,
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier
                .height(56.dp)
                .padding(start = 20.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) { Text(text = stringResource(id = R.string.choco_bridge_bar_title)) }
            IconButton(onClick = onSettingClick) {
                Icon(painter = painterResource(id = R.drawable.ic_outline_settings_24), contentDescription = "設定")
            }
        }
    }
}


/**
 * 検索欄
 *
 * @param modifier Modifier
 * @param textValue テキストボックスの中身
 * @param onTextChange テキストボックスの中身が変わったら呼ばれる
 * @param onSearchClick 検索押したら
 * @param onBackClick 戻る押したら呼ばれる
 * @param isFocusTextBox テキストボックスにフォーカスが当たっていればtrue
 * @param focusRequester フォーカスのやつ
 * @param onFocusChange フォーカスが変わったら呼ばれる
 * @param suggestContent サジェスト用に
 * */
@Composable
fun ChocoBridgeBar(
    modifier: Modifier = Modifier,
    textValue: TextFieldValue,
    isFocusTextBox: Boolean,
    focusRequester: FocusRequester,
    onBackClick: () -> Unit,
    onTextChange: (TextFieldValue) -> Unit,
    onSearchClick: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    suggestContent: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        color = MaterialTheme.colorScheme.inversePrimary,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(56.dp)
            ) {
                IconButton(
                    modifier = Modifier
                        .padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
                        .focusTarget(),
                    onClick = onBackClick,
                ) { Icon(painter = painterResource(id = R.drawable.ic_outline_arrow_back_24), contentDescription = "クリア") }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (textValue.text.isEmpty() && !isFocusTextBox) {
                        // ヒント代わり
                        Text(text = stringResource(id = R.string.choco_bridge_bar_title))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .onFocusChanged { onFocusChange(it.isFocused) },
                            value = textValue,
                            maxLines = 1,
                            singleLine = true,
                            textStyle = TextStyle(color = LocalContentColor.current),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { onSearchClick.invoke(textValue.text) }),
                            onValueChange = onTextChange,
                        )
                        // クリアボタン
                        if (isFocusTextBox && textValue.text.isNotEmpty()) {
                            IconButton(
                                modifier = Modifier
                                    .padding(end = 10.dp),
                                onClick = { onTextChange(TextFieldValue()) }
                            ) { Icon(painter = painterResource(id = R.drawable.ic_outline_clear_24), contentDescription = "クリア") }
                        }
                    }
                }
            }
            suggestContent()
        }
    }
}

/**
 * 検索押したときに検索とかのボタン
 *
 * @param onClick 押したとき
 * @param resIconId アイコンリソースID
 * @param text テキスト
 * */
@Composable
fun ChocoBridgeItem(resIconId: Int, text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent,
        content = {
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
    )
}

/**
 * 検索サジェスト用のレイアウト
 *
 * @param text サジェスト
 * @param onSearchClick 押したとき、検索ワードが入ってます
 * @param onAppendClick テキストボックスに入れる場合に呼ばれる
 * */
@Composable
fun ChocoBridgeSuggestItem(text: String, onSearchClick: (String) -> Unit, onAppendClick: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSearchClick(text) },
        color = Color.Transparent,
        content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    text = text
                )
                IconButton(
                    modifier = Modifier.padding(end = 10.dp),
                    onClick = { onAppendClick(text) }
                ) { Icon(painter = painterResource(id = R.drawable.ic_baseline_north_west_24), contentDescription = null) }
            }
        }
    )
}
