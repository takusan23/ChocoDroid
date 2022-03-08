package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.chocodroid.R

/**
 * 検索バー。検索ボックスとか並び替えとかある
 *
 * @param onBack 戻る押したら呼ぶ
 * @param onSearchBarClick 検索欄を押したら呼ばれる
 * @param searchWord 検索ワード
 * @param onSortChange 並び順ボタンを押したら呼ばれる
 * */
@Composable
fun SearchScreenBar(
    onBack: () -> Unit,
    onSearchBarClick: () -> Unit,
    onSortChange: () -> Unit,
    searchWord: String,
) {
    Surface(
        modifier = Modifier
            .padding(10.dp)
            .clickable(onClick = onSearchBarClick),
        color = MaterialTheme.colorScheme.inversePrimary,
        shape = RoundedCornerShape(20.dp),
        content = {
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
                    ) { Text(modifier = Modifier.padding(5.dp), text = searchWord) }
                    IconButton(
                        modifier = Modifier.padding(5.dp),
                        onClick = onSortChange,
                        content = { Icon(painter = painterResource(id = R.drawable.ic_outline_sort_24), contentDescription = null) }
                    )
                }
            }
        }
    )
}
