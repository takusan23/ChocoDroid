package io.github.takusan23.chocodroid.ui.screen.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.tool.SearchSortScreenTool
import io.github.takusan23.chocodroid.viewmodel.SearchSortScreenViewModel

/**
 * 検索の並び替えボトムシート画面
 *
 * @param viewModel ViewModel
 * @param onClose 閉じてほしいときに呼ばれる
 * */
@Composable
fun SearchSortScreen(
    viewModel: SearchSortScreenViewModel = viewModel(),
    onClose: () -> Unit,
) {
    // 現在のソートの種類
    val currentSortType = viewModel.currentSortType.collectAsState(initial = null)

    // 並び替えの種類
    val sortTypeList = listOf(
        Triple(R.drawable.ic_outline_auto_graph_24, stringResource(id = R.string.search_sort_relevance), SearchSortScreenTool.SearchSortType.Relevance),
        Triple(R.drawable.ic_outline_today_24, stringResource(id = R.string.search_sort_post_date), SearchSortScreenTool.SearchSortType.UploadDate),
        Triple(R.drawable.ic_outline_play_arrow_24, stringResource(id = R.string.search_sort_view_count), SearchSortScreenTool.SearchSortType.WatchCount),
        Triple(R.drawable.ic_outline_thumbs_up_down_24, stringResource(id = R.string.search_sort_review), SearchSortScreenTool.SearchSortType.Review),
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            Text(
                modifier = Modifier.padding(10.dp),
                text = stringResource(id = R.string.search_sort),
                fontSize = 25.sp
            )
            sortTypeList.forEach { (iconResId, text, type) ->
                SearchSortTypeItem(
                    iconResId = iconResId,
                    text = text,
                    type = type,
                    isSelected = SearchSortScreenTool.serialize(type) == currentSortType.value,
                    onClick = {
                        viewModel.setSortType(it)
                        onClose()
                    }
                )
            }
        },
    )
}

/**
 * 並び替えアイテム
 *
 * @param iconResId アイコンのリソースID
 * @param text テキスト
 * @param type ソートの種類
 * @param onClick 押した際に呼ばれる
 * @param isSelected 選択中ならtrue
 * */
@Composable
private fun SearchSortTypeItem(
    iconResId: Int,
    text: String,
    type: SearchSortScreenTool.SearchSortType,
    isSelected: Boolean,
    onClick: (SearchSortScreenTool.SearchSortType) -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = { onClick(type) },
            ),
        color = Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        contentColor = if (isSelected) MaterialTheme.colorScheme.primary else contentColorFor(MaterialTheme.colorScheme.surface),
        content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.padding(10.dp),
                    painter = painterResource(id = iconResId),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    text = text,
                    fontSize = 25.sp,
                )
            }
        }
    )
}

/** 検索並び替えボトムシート画面を出す際に使う */
class SearchSortScreenInitData : BottomSheetInitData(BottomSheetScreenList.SearchSortChange)