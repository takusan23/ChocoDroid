package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.internet.api.SearchAPI

/**
 * 検索バー。検索ボックスとか並び替えとかある
 *
 * @param onBack 戻る押したら呼ぶ
 * @param onSearch 検索押したら呼ばれる
 * @param searchWord 検索ワード
 * @param onSearchWordChange 検索ワード変更したら呼ばれる
 * @param sort 並び順。[SearchAPI.PARAMS_SORT_REVIEW]など
 * @param onSort 並び順変更したら呼ばれる
 * */
@Composable
fun SearchScreenBar(
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    onSort: (String) -> Unit,
    onSearchWordChange: (String) -> Unit,
    searchWord: String,
    sort: String = SearchAPI.PARAMS_SORT_RELEVANCE,
) {
    val isFocusTextField = remember { mutableStateOf(false) }
    val isShowSortMenu = remember { mutableStateOf(false) }
    // 並び順
    val sortList = listOf(
        SearchAPI.PARAMS_SORT_RELEVANCE,
        SearchAPI.PARAMS_SORT_UPLOAD_DATE,
        SearchAPI.PARAMS_SORT_WATCH_COUNT,
        SearchAPI.PARAMS_SORT_REVIEW
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                modifier = Modifier.padding(5.dp),
                onClick = onBack,
                content = { Icon(painter = painterResource(id = R.drawable.ic_outline_arrow_back_24), contentDescription = null) }
            )
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                if (searchWord.isEmpty()) {
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = "検索ワード"
                    )
                }
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocusTextField.value = it.isFocused }
                        .padding(5.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    singleLine = true,
                    keyboardActions = KeyboardActions(onSearch = { onSearch(searchWord) }),
                    value = searchWord,
                    onValueChange = onSearchWordChange
                )
            }
            // 消すボタン
            if (isFocusTextField.value) {
                IconButton(
                    modifier = Modifier.padding(5.dp),
                    onClick = { onSearchWordChange("") },
                    content = { Icon(painter = painterResource(id = R.drawable.ic_outline_clear_24), contentDescription = null) }
                )
            }
            IconButton(
                modifier = Modifier.padding(5.dp),
                onClick = { isShowSortMenu.value = !isShowSortMenu.value },
                content = { Icon(painter = painterResource(id = R.drawable.ic_outline_sort_24), contentDescription = null) }
            )
            IconButton(
                modifier = Modifier.padding(5.dp),
                onClick = { onSearch(searchWord) },
                content = { Icon(painter = painterResource(id = R.drawable.ic_outline_search_24), contentDescription = null) }
            )
        }
        // ソートメニュー表示
        if (isShowSortMenu.value) {
            SearchSortToggleButton(
                modifier = Modifier
                    .padding(5.dp)
                    .align(alignment = Alignment.End),
                selectedItemPos = sortList.indexOf(sort),
                onClick = { onSort(sortList[it]) }
            )
        }
    }
}
