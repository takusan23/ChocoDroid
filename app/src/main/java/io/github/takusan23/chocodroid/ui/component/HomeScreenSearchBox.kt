package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.chocodroid.R

/**
 * 簡略版。
 * @param onEnterVideoId 動画IDが決定したら呼ばれる
 * */
@Composable
fun HomeScreenSearchBox(
    modifier: Modifier = Modifier,
    onEnterVideoId: (String) -> Unit,
) {
    val videoId = remember { mutableStateOf("") }
    HomeScreenSearchBox(
        modifier = modifier,
        videoId = videoId.value,
        onChangeVideoId = { after -> videoId.value = after },
        onClickEnter = onEnterVideoId
    )
}


/**
 * 最初の画面に置く検索ボックス。今の所動画ID入力欄だけど他にも機能を足したい
 *
 * @param videoId テキストボックスの中身
 * @param onChangeVideoId テキストボックスの中身が変わったら呼ばれる
 * @param onClickEnter 再生ボタンが呼ばれたら
 * @param modifier Modifier
 * */
@Composable
fun HomeScreenSearchBox(
    modifier: Modifier = Modifier,
    videoId: String,
    onChangeVideoId: (String) -> Unit,
    onClickEnter: ((String) -> Unit)? = null,
) {
    // テキストボックスにフォーカスがあたっているか
    val isFocusTextBox = remember { mutableStateOf(false) }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        color = MaterialTheme.colors.primary,
        shape = RoundedCornerShape(50),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 20.dp, top = 5.dp, end = 5.dp, bottom = 5.dp)
                    .weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (videoId.isEmpty() || !isFocusTextBox.value) {
                    // ヒント代わり
                    Text(text = "動画のID / URL を入力")
                }
                BasicTextField(
                    value = videoId,
                    maxLines = 1,
                    onValueChange = onChangeVideoId,
                    modifier = Modifier.fillMaxWidth().onFocusChanged { isFocusTextBox.value = it.isFocused }
                )
            }
            IconButton(
                modifier = Modifier.padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
                onClick = { onClickEnter?.invoke(videoId) },
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_outline_search_24),
                        contentDescription = "search"
                    )
                }
            )
        }
    }
}