package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.database.entity.FavoriteFolderDBEntity
import io.github.takusan23.chocodroid.tool.TimeFormatTool

/**
 * お気に入りフォルダ一覧
 *
 * @param modifier Modifier
 * @param lazyListState スクロール制御
 * @param list お気に入りフォルダのデータクラス
 * @param onClick 項目押したら呼ばれる。[FavoriteFolderDBEntity.id]を入れます
 * */
@Composable
fun FavoriteFolderList(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    list: List<FavoriteFolderDBEntity>,
    onClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        content = {
            items(list) { item ->
                FavoriteFolderItem(item, onClick)
            }
        }
    )
}

/**
 * お気に入りフォルダ一覧表示で使う各項目
 *
 * @param favoriteFolderDBEntity お気に入りデータ
 * @param onClick 項目押したら呼ばれる。[FavoriteFolderDBEntity.id]を入れます
 * */
@Composable
private fun FavoriteFolderItem(
    favoriteFolderDBEntity: FavoriteFolderDBEntity,
    onClick: (Int) -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = { onClick(favoriteFolderDBEntity.id) },
            ),
        color = Color.Transparent
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier
                    .padding(10.dp),
                painter = painterResource(id = R.drawable.ic_outline_folder_special_24),
                contentDescription = null
            )
            Column {
                Text(
                    text = favoriteFolderDBEntity.folderName,
                    fontSize = 18.sp,
                    maxLines = 2
                )
                Text(
                    text = TimeFormatTool.unixTimeToFormatText(favoriteFolderDBEntity.updateDate),
                    fontSize = 14.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
    }
}