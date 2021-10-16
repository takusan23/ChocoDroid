package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

/**
 * 動画一覧で使う各項目のコンポーネント
 * */
@ExperimentalMaterialApi
@Composable
fun VideoListItem(
    videoId: String,
    title: String,
    thumbnailUrl: String,
    ownerName: String,
    onClick: (String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(videoId) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Glideの代わりにCoilを試す
            Image(
                modifier = Modifier
                    .width(100.dp)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .aspectRatio(1.7f),
                painter = rememberImagePainter(
                    data = thumbnailUrl,
                    builder = {
                        crossfade(true)
                    }
                ),
                contentDescription = null
            )
            Column {
                Text(
                    modifier = Modifier.padding(5.dp),
                    fontSize = 16.sp,
                    text = title,
                    maxLines = 2
                )
                Text(
                    modifier = Modifier.padding(5.dp),
                    fontSize = 14.sp,
                    text = ownerName
                )
            }
        }
    }
}