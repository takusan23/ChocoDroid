package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 画質選択一覧
 *
 * @param currentQualityLabel 現在の画質
 * @param qualityLabelList 画質一覧
 * @param onQualityClick 画質選択時に呼ばれる
 * */
@Composable
fun QualityList(
    currentQualityLabel: String = "360p",
    qualityLabelList: List<String>,
    onQualityClick: (String) -> Unit,
) {
    LazyColumn(content = {
        items(qualityLabelList) {
            QualityListItem(
                label = it,
                isSelected = it == currentQualityLabel,
                onClick = { onQualityClick(it) }
            )
        }
    })
}

/**
 * 画質一覧の各項目
 *
 * @param isSelected 現在選択中の画質の場合はtrue
 * @param label 画質の名前
 * @param onClick 押したとき
 * */
@Composable
private fun QualityListItem(
    label: String,
    onClick: () -> Unit,
    isSelected: Boolean = false,
) {
    Surface(
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            text = label,
            fontSize = 25.sp,
            textAlign = TextAlign.Start,
            style = TextStyle(color = if (isSelected) MaterialTheme.colorScheme.primary else LocalTextStyle.current.color)
        )
    }
}